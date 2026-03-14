package com.walletiq.service.impl;

import com.walletiq.dto.mail.DailySummaryMailData;
import com.walletiq.dto.transaction.CreateTransactionRequest;
import com.walletiq.dto.transaction.TransactionFilterRequest;
import com.walletiq.dto.transaction.TransactionResponse;
import com.walletiq.dto.transaction.UpdateTransactionRequest;
import com.walletiq.entity.Category;
import com.walletiq.entity.PaymentMode;
import com.walletiq.entity.Transaction;
import com.walletiq.entity.User;
import com.walletiq.enums.CategoryType;
import com.walletiq.enums.ErrorCode;
import com.walletiq.enums.TxnType;
import com.walletiq.exception.CategoryException;
import com.walletiq.exception.PaymentModeException;
import com.walletiq.exception.TransactionException;
import com.walletiq.mapper.TransactionMapper;
import com.walletiq.repository.CategoryRepository;
import com.walletiq.repository.PaymentModeRepository;
import com.walletiq.repository.TransactionRepository;
import com.walletiq.repository.UserRepository;
import com.walletiq.service.BudgetAlertService;
import com.walletiq.service.EmbeddingService;
import com.walletiq.service.TransactionService;
import com.walletiq.util.PageableValidator;
import com.walletiq.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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

        UUID categoryId = filter.categoryId() != null ? UUID.fromString(filter.categoryId()) : null;

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

    /**
     * Builds daily summary email data.
     */
    @Override
    @Transactional(readOnly = true)
    public DailySummaryMailData buildDailySummaryMailData(User user) {
        LocalDate today = LocalDate.now();

        List<Transaction> todaysTransactions = transactionRepository.findByUserAndDate(user, today);

        // Calculate the income and expenses
        BigDecimal income = todaysTransactions.stream()
            .filter(t -> t.getType() == TxnType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = todaysTransactions.stream()
            .filter(t -> t.getType() == TxnType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DailySummaryMailData(
            user.getFullName(),
            user.getEmail(),
            today.format(DATE_FORMAT),
            income,
            expenses,
            income.subtract(expenses),
            todaysTransactions.size()
        );
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
    private Category resolveCategory(String categoryId,
                                     User user,
                                     CategoryType categoryType) {

        UUID id = UUID.fromString(categoryId);

        return categoryRepository
            .findAllVisibleToUser(user, categoryType)
            .stream()
            .filter(c -> c.getId().equals(id))
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
    private PaymentMode resolvePaymentMode(String paymentModeId,
                                           User user) {

        UUID id = UUID.fromString(paymentModeId);

        return paymentModeRepository
            .findAllVisibleToUser(user)
            .stream()
            .filter(p -> p.getId().equals(id))
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