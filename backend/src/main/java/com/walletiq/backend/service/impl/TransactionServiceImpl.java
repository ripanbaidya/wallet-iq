package com.walletiq.backend.service.impl;

import com.walletiq.backend.dto.transaction.CreateTransactionRequest;
import com.walletiq.backend.dto.transaction.TransactionFilterRequest;
import com.walletiq.backend.dto.transaction.TransactionResponse;
import com.walletiq.backend.dto.transaction.UpdateTransactionRequest;
import com.walletiq.backend.entity.Category;
import com.walletiq.backend.entity.PaymentMode;
import com.walletiq.backend.entity.Transaction;
import com.walletiq.backend.entity.User;
import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.enums.TxnType;
import com.walletiq.backend.exception.CategoryException;
import com.walletiq.backend.exception.PaymentModeException;
import com.walletiq.backend.exception.TransactionException;
import com.walletiq.backend.mapper.TransactionMapper;
import com.walletiq.backend.repository.CategoryRepository;
import com.walletiq.backend.repository.PaymentModeRepository;
import com.walletiq.backend.repository.TransactionRepository;
import com.walletiq.backend.repository.UserRepository;
import com.walletiq.backend.service.EmbeddingService;
import com.walletiq.backend.service.TransactionService;
import com.walletiq.backend.util.PageableValidator;
import com.walletiq.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentModeRepository paymentModeRepository;
    private final UserRepository userRepository;
    private final EmbeddingService embeddingService;

    /**
     * Returns transactions for the current user using optional filters.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(TransactionFilterRequest filter,
                                                        Pageable pageable) {
        User currentUser = currentUser();

        UUID categoryId = filter.categoryId() != null
            ? UUID.fromString(filter.categoryId())
            : null;

        Pageable safePageable = PageableValidator.validateTransactionPageable(pageable);

        Page<Transaction> page = transactionRepository.findAllByFilter(
            currentUser, filter.type(), categoryId,
            filter.dateFrom(), filter.dateTo(), pageable
        );

        return page.map(TransactionMapper::toResponse);
    }

    /**
     * Returns a single transaction belonging to the current user.
     */
    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID id) {
        User currentUser = currentUser();
        Transaction transaction = findTransactionByIdAndUser(id, currentUser);

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
            transaction.setCategory(
                resolveCategory(request.categoryId(), currentUser)
            );
            transaction.setPaymentMode(
                resolvePaymentMode(request.paymentModeId(), currentUser)
            );
        }

        transaction = transactionRepository.save(transaction);

        // Store embedding (no-op until AI service is implemented)
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
        Transaction transaction = findTransactionByIdAndUser(id, currentUser);

        // Determine the effective type and identifiers after update.
        // This ensures validation works even for partial updates.
        TxnType effectiveType = request.type() != null ? request.type() : transaction.getType();

        String effectiveCategoryId = request.categoryId() != null
            ? request.categoryId() : (transaction.getCategory() != null
            ? transaction.getCategory().getId().toString() : null);

        String effectivePaymentModeId = request.paymentModeId() != null
            ? request.paymentModeId() : (transaction.getPaymentMode() != null
            ? transaction.getPaymentMode().getId().toString() : null);

        // Validate required fields if transaction is EXPENSE
        validateExpenseFields(effectiveType, effectiveCategoryId, effectivePaymentModeId);

        // Apply only provided fields
        if (request.amount() != null) {
            transaction.setAmount(request.amount());
        }

        if (request.type() != null) {
            transaction.setType(request.type());
        }

        if (request.date() != null) {
            transaction.setDate(request.date());
        }

        if (request.note() != null) {
            transaction.setNote(request.note());
        }

        if (request.categoryId() != null) {
            transaction.setCategory(
                resolveCategory(request.categoryId(), currentUser)
            );
        }

        if (request.paymentModeId() != null) {
            transaction.setPaymentMode(
                resolvePaymentMode(request.paymentModeId(), currentUser)
            );
        }

        // If switched to INCOME, category and payment mode are irrelevant
        if (effectiveType == TxnType.INCOME) {
            transaction.setCategory(null);
            transaction.setPaymentMode(null);
        }

        // Update embedding
        String newEmbeddingId = embeddingService.update(transaction.getEmbeddingId(), transaction);

        if (newEmbeddingId != null) {
            transaction.setEmbeddingId(newEmbeddingId);
        }

        return TransactionMapper.toResponse(transactionRepository.save(transaction));
    }

    /**
     * Deletes a transaction belonging to the current user.
     */
    @Override
    @Transactional
    public void deleteTransaction(UUID id) {
        User currentUser = currentUser();
        Transaction transaction = findTransactionByIdAndUser(id, currentUser);

        // Remove embedding if present
        embeddingService.delete(transaction.getEmbeddingId());

        transactionRepository.delete(transaction);
    }

    // Helper Methods

    /**
     * Returns the currently authenticated user reference.
     */
    private User currentUser() {
        return userRepository.getReferenceById(SecurityUtils.getCurrentUserId());
    }

    /**
     * Finds a transaction belonging to the given user.
     */
    private Transaction findTransactionByIdAndUser(UUID id, User user) {
        return transactionRepository
            .findByIdAndUser(id, user)
            .orElseThrow(() -> new TransactionException(
                ErrorCode.TRANSACTION_NOT_FOUND,
                "Transaction not found or you do not have permission to access it"
            ));
    }

    /**
     * Ensures that EXPENSE transactions include category and payment mode.
     */
    private void validateExpenseFields(TxnType type, String categoryId, String paymentModeId) {
        if (type == TxnType.EXPENSE) {
            if (categoryId == null || categoryId.isBlank()) {
                throw new TransactionException(
                    ErrorCode.INVALID_TRANSACTION,
                    "Category is required for EXPENSE transactions"
                );
            }
            if (paymentModeId == null || paymentModeId.isBlank()) {
                throw new TransactionException(
                    ErrorCode.INVALID_TRANSACTION,
                    "Payment mode is required for EXPENSE transactions"
                );
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
            .orElseThrow(() -> new CategoryException(
                ErrorCode.CATEGORY_NOT_FOUND,
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
            .orElseThrow(() -> new PaymentModeException(
                ErrorCode.PAYMENT_MODE_NOT_FOUND,
                "Payment mode not found or not accessible"
            ));
    }
}