package online.walletiq.service.impl;

import online.walletiq.constant.CacheNames;
import online.walletiq.dto.transaction.CreateTransactionRequest;
import online.walletiq.dto.transaction.TransactionFilterRequest;
import online.walletiq.dto.transaction.TransactionResponse;
import online.walletiq.dto.transaction.UpdateTransactionRequest;
import online.walletiq.entity.Category;
import online.walletiq.entity.PaymentMode;
import online.walletiq.entity.Transaction;
import online.walletiq.entity.User;
import online.walletiq.enums.CategoryType;
import online.walletiq.enums.ErrorCode;
import online.walletiq.enums.TxnType;
import online.walletiq.exception.CategoryException;
import online.walletiq.exception.PaymentModeException;
import online.walletiq.exception.TransactionException;
import online.walletiq.mapper.TransactionMapper;
import online.walletiq.repository.CategoryRepository;
import online.walletiq.repository.PaymentModeRepository;
import online.walletiq.repository.TransactionRepository;
import online.walletiq.repository.UserRepository;
import online.walletiq.service.BudgetAlertService;
import online.walletiq.service.EmbeddingService;
import online.walletiq.service.TransactionService;
import online.walletiq.util.PageableValidator;
import online.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Default implementation of {@link TransactionService}.
 * <p>Handles transaction lifecycle including creation, update, deletion,
 * filtering, and summary generation.
 * <p>Also integrates with embedding, budgeting, and notification systems
 * as side effects of transaction operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");

    private final EmbeddingService embeddingService;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetAlertService budgetAlertService;
    private final TransactionRepository transactionRepository;
    private final PaymentModeRepository paymentModeRepository;

    /**
     * Returns transactions for the current user using optional filters.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(TransactionFilterRequest filter,
                                                        Pageable pageable) {
        User currentUser = currentUser();

        UUID categoryId = filter.categoryId() != null ? filter.categoryId() : null;

        Pageable safePageable = PageableValidator.validateTransactionPageable(pageable);

        Page<Transaction> page = transactionRepository.findAllByFilter(
            currentUser, filter.type(), categoryId,
            filter.dateFrom(), filter.dateTo(), safePageable
        );

        return page.map(TransactionMapper::toResponse);
    }

    /**
     * Returns a single transaction belonging to the current user.
     */
    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID id) {
        User user = currentUser();

        Transaction transaction = findTransactionByIdAndUser(id, user.getId());

        return TransactionMapper.toResponse(transaction);
    }

    /**
     * Creates a new transaction.
     * Category and payment mode are required only for EXPENSE transactions.
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheNames.DASHBOARD, keyGenerator = "transactionDateKeyGenerator")
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        User currentUser = currentUser();

        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setDate(request.date());
        transaction.setNote(request.note());
        transaction.setUser(currentUser);

        CategoryType categoryType = getCategoryType(request.type());

        transaction.setCategory(resolveCategory(request.categoryId(), currentUser, categoryType));
        transaction.setPaymentMode(resolvePaymentMode(request.paymentModeId(), currentUser));

        transaction = transactionRepository.saveAndFlush(transaction);

        // Check for budget alerts
        if (transaction.getType() == TxnType.EXPENSE && transaction.getCategory() != null) {
            budgetAlertService.checkAndAlert(
                currentUser.getId(),
                transaction.getCategory().getId(),
                YearMonth.from(transaction.getDate()));
        }

        String embeddingId = embeddingService.store(transaction);

        if (embeddingId != null) {
            transaction.setEmbeddingId(embeddingId);
            transaction = transactionRepository.save(transaction);
        }

        return TransactionMapper.toResponse(transaction);
    }

    /**
     * Updates an existing transaction, supports partial updates.
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheNames.DASHBOARD, keyGenerator = "transactionDateKeyGenerator")
    public TransactionResponse updateTransaction(UUID id, UpdateTransactionRequest request) {
        User user = currentUser();

        Transaction transaction = findTransactionByIdAndUser(id, user.getId());

        TxnType effectiveType = request.type() != null ? request.type() : transaction.getType();

        Category category = transaction.getCategory();
        if (request.categoryId() != null) {
            category = resolveCategory(request.categoryId(), user,
                getCategoryType(effectiveType)
            );
        }

        PaymentMode paymentMode = transaction.getPaymentMode();
        if (request.paymentModeId() != null) {
            paymentMode = resolvePaymentMode(request.paymentModeId(), user);
        }

        if (category != null &&
            category.getCategoryType() != getCategoryType(effectiveType)) {

            throw new TransactionException(ErrorCode.INVALID_CATEGORY_TYPE,
                "Category type does not match transaction type"
            );
        }

        if (request.amount() != null) transaction.setAmount(request.amount());
        if (request.type() != null) transaction.setType(request.type());
        if (request.date() != null) transaction.setDate(request.date());
        if (request.note() != null) transaction.setNote(request.note());

        if (effectiveType == TxnType.INCOME) {
            transaction.setCategory(null);
            transaction.setPaymentMode(null);
        } else {
            transaction.setCategory(category);
            transaction.setPaymentMode(paymentMode);
        }

        // Make sure flush it, so the sum query in checkAndAlert sees the
        // updated amount/category/date
        transactionRepository.flush();

        // Check for budget alerts
        if (effectiveType == TxnType.EXPENSE && transaction.getCategory() != null) {
            budgetAlertService.checkAndAlert(
                user.getId(),
                transaction.getCategory().getId(),
                YearMonth.from(transaction.getDate())
            );
        }

        String newEmbeddingId = embeddingService.update(transaction.getEmbeddingId(), transaction);
        if (newEmbeddingId != null) {
            transaction.setEmbeddingId(newEmbeddingId);
        }

        return TransactionMapper.toResponse(transaction);
    }

    /**
     * Deletes a transaction belonging to the current user.
     */
    @Override
    @Transactional
    public void deleteTransaction(UUID transactionId) {
        User currentUser = currentUser();

        Transaction transaction = findTransactionByIdAndUser(transactionId, currentUser.getId());

        embeddingService.delete(transaction.getEmbeddingId());

        transactionRepository.delete(transaction);
    }

    // Helper Methods

    /**
     * Returns the currently authenticated user.
     */
    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    /**
     * Finds a transaction belonging to the given user.
     */
    private Transaction findTransactionByIdAndUser(UUID id, UUID userId) {
        return transactionRepository
            .findByIdAndUser_Id(id, userId)
            .orElseThrow(() ->
                new TransactionException(ErrorCode.TRANSACTION_NOT_FOUND,
                    "Transaction not found or access denied"
                ));
    }

    /**
     * Resolves a category accessible to the user.
     */
    private Category resolveCategory(UUID categoryId,
                                     User user,
                                     CategoryType categoryType) {

        return categoryRepository
            .findAllVisibleToUser(user, categoryType)
            .stream()
            .filter(c -> c.getId().equals(categoryId))
            .findFirst()
            .orElseThrow(() ->
                new CategoryException(
                    ErrorCode.CATEGORY_NOT_FOUND,
                    "Category not found or not accessible"
                ));
    }

    /**
     * Resolves a payment mode accessible to the user.
     */
    private PaymentMode resolvePaymentMode(UUID paymentModeId,
                                           User user) {

        return paymentModeRepository
            .findAllVisibleToUser(user)
            .stream()
            .filter(p -> p.getId().equals(paymentModeId))
            .findFirst()
            .orElseThrow(() ->
                new PaymentModeException(
                    ErrorCode.PAYMENT_MODE_NOT_FOUND,
                    "Payment mode not found or not accessible"
                ));
    }

    /**
     * Maps transaction type to category type.
     */
    private CategoryType getCategoryType(TxnType txnType) {
        return txnType == TxnType.INCOME
            ? CategoryType.INCOME
            : CategoryType.EXPENSE;
    }
}