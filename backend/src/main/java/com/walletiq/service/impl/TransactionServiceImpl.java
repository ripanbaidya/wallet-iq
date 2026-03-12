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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");

    private final EmbeddingService embeddingService;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentModeRepository paymentModeRepository;

    // Implementations

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

        Page<Transaction> page = transactionRepository.findAllByFilter(currentUser, filter.type(),
            categoryId, filter.dateFrom(), filter.dateTo(), safePageable);

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

        // Validate required fields for EXPENSE transactions
        validateExpenseFields(request.type(), request.categoryId(), request.paymentModeId());

        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setDate(request.date());
        transaction.setNote(request.note());
        transaction.setUser(currentUser);

        // Only set category and payment mode for EXPENSE transactions
        if (request.type() == TxnType.EXPENSE) {
            transaction.setCategory(resolveCategory(request.categoryId(), currentUser));
            transaction.setPaymentMode(resolvePaymentMode(request.paymentModeId(), currentUser));
        }

        transaction = transactionRepository.save(transaction);

        // Store embedding
        String embeddingId = embeddingService.store(transaction);

        if (embeddingId != null) {
            transaction.setEmbeddingId(embeddingId);
            transaction = transactionRepository.save(transaction);
        }

        return TransactionMapper.toResponse(transaction);
    }

    /**
     * Updates an existing transaction.
     * Supports partial updates — only provided fields are modified.
     */
    @Override
    @Transactional
    public TransactionResponse updateTransaction(UUID id, UpdateTransactionRequest request) {
        User currentUser = currentUser();
        Transaction transaction = findTransactionByIdAndUser(id, currentUser.getId());

        // Determine the effective type and identifiers after update.
        // This ensures validation works even for partial updates.
        TxnType effectiveType = request.type() != null ? request.type() : transaction.getType();

        String effectiveCategoryId = request.categoryId() != null ? request.categoryId()
            : (transaction.getCategory() != null ? transaction.getCategory().getId().toString() : null);

        String effectivePaymentModeId = request.paymentModeId() != null ? request.paymentModeId() :
            (transaction.getPaymentMode() != null ? transaction.getPaymentMode().getId().toString() : null);

        // Validate required fields if transaction is EXPENSE
        validateExpenseFields(effectiveType, effectiveCategoryId, effectivePaymentModeId);

        // Apply only provided fields
        if (request.amount() != null) transaction.setAmount(request.amount());
        if (request.type() != null) transaction.setType(request.type());
        if (request.date() != null) transaction.setDate(request.date());
        if (request.note() != null) transaction.setNote(request.note());

        if (request.categoryId() != null) transaction.setCategory(resolveCategory(request.categoryId(), currentUser));
        if (request.paymentModeId() != null)
            transaction.setPaymentMode(resolvePaymentMode(request.paymentModeId(), currentUser));

        // If switched to INCOME, category and payment mode are irrelevant
        if (effectiveType == TxnType.INCOME) {
            transaction.setCategory(null);
            transaction.setPaymentMode(null);
        }

        // Update embedding
        String newEmbeddingId = embeddingService.update(transaction.getEmbeddingId(), transaction);
        if (newEmbeddingId != null) transaction.setEmbeddingId(newEmbeddingId);

        return TransactionMapper.toResponse(transactionRepository.save(transaction));
    }

    /**
     * Deletes a transaction belonging to the current user.
     */
    @Override
    @Transactional
    public void deleteTransaction(UUID transactionId) {
        User currentUser = currentUser();
        Transaction transaction = findTransactionByIdAndUser(transactionId, currentUser.getId());

        // Remove embedding if present
        embeddingService.delete(transaction.getEmbeddingId());

        transactionRepository.delete(transaction);
    }

    @Override
    public DailySummaryMailData buildDailySummaryMailData(User user) {
        LocalDate today = LocalDate.now();
        List<Transaction> todaysTransaction = transactionRepository.findByUserAndDate(user, today);

        // Extract income and expense amounts
        BigDecimal income = todaysTransaction.stream()
            .filter(t -> t.getType() == TxnType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = todaysTransaction.stream()
            .filter(t -> t.getType() == TxnType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DailySummaryMailData(user.getFullName(), user.getEmail(), today.format(DATE_FORMAT),
            income, expenses, income.subtract(expenses), todaysTransaction.size());
    }

    // Helper methods

    /**
     * Returns the currently authenticated user reference.
     */
    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    /**
     * Finds a transaction belonging to the given user.
     */
    private Transaction findTransactionByIdAndUser(UUID id, UUID userId) {
        return transactionRepository.findByIdAndUser_Id(id, userId)
            .orElseThrow(() -> new TransactionException(ErrorCode.TRANSACTION_NOT_FOUND,
                "Transaction not found or you do not have permission to access it"
            ));
    }

    /**
     * Ensures that EXPENSE transactions include category and payment mode.
     */
    private void validateExpenseFields(TxnType type, String categoryId, String paymentModeId) {
        if (type == TxnType.EXPENSE) {
            if (categoryId == null || categoryId.isBlank()) {
                throw new TransactionException(ErrorCode.INVALID_TRANSACTION, "Category is required for EXPENSE transactions");
            }

            if (paymentModeId == null || paymentModeId.isBlank()) {
                throw new TransactionException(ErrorCode.INVALID_TRANSACTION, "Payment mode is required for EXPENSE transactions");
            }
        }
    }

    /**
     * Resolves a category accessible to the current user.
     * Category may belong to the user or be a system default.
     */
    private Category resolveCategory(String categoryId, User currentUser) {
        UUID id = UUID.fromString(categoryId);
        return categoryRepository
            .findAllVisibleToUser(currentUser).stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found or not accessible"
            ));
    }

    /**
     * Resolves a payment mode accessible to the current user.
     * Payment mode may belong to the user or be a system default.
     */
    private PaymentMode resolvePaymentMode(String paymentModeId, User currentUser) {
        UUID id = UUID.fromString(paymentModeId);
        return paymentModeRepository
            .findAllVisibleToUser(currentUser).stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new PaymentModeException(ErrorCode.PAYMENT_MODE_NOT_FOUND,
                "Payment mode not found or not accessible"
            ));
    }
}