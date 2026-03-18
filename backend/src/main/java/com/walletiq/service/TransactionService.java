package com.walletiq.service;

import com.walletiq.dto.mail.DailySummaryMailData;
import com.walletiq.dto.transaction.CreateTransactionRequest;
import com.walletiq.dto.transaction.TransactionFilterRequest;
import com.walletiq.dto.transaction.TransactionResponse;
import com.walletiq.dto.transaction.UpdateTransactionRequest;
import com.walletiq.entity.User;
import com.walletiq.exception.CategoryException;
import com.walletiq.exception.PaymentModeException;
import com.walletiq.exception.TransactionException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface TransactionService {

    /**
     * Retrieves transactions for the current user using optional filters.
     *
     * @param filter   filtering criteria (type, category, date range)
     * @param pageable pagination information
     * @return paginated list of transactions
     * @throws EntityNotFoundException if current user is not found
     */
    Page<TransactionResponse> getAllTransactions(
        TransactionFilterRequest filter,
        Pageable pageable
    );

    /**
     * Retrieves a transaction by ID for the current user.
     *
     * @param transactionId transaction identifier
     * @return transaction details
     * @throws TransactionException if the transaction is not found
     *                              or does not belong to the user
     */
    TransactionResponse getTransactionById(UUID transactionId);

    /**
     * Creates a new transaction for the current user.
     * <p>For EXPENSE transactions, category and payment mode must be valid
     * and accessible to the user. Also triggers budget checks and embedding.
     *
     * @param request transaction creation request
     * @return created transaction response
     * @throws CategoryException                           if category is not found or not accessible
     * @throws PaymentModeException                        if payment mode is not found or not accessible
     * @throws jakarta.persistence.EntityNotFoundException if current user is not found
     */
    TransactionResponse createTransaction(CreateTransactionRequest request);

    /**
     * Updates an existing transaction belonging to the current user.
     * <p>Supports partial updates. Also updates embedding and triggers
     * budget checks if applicable.
     *
     * @param transactionId transaction identifier
     * @param request       update request
     * @return updated transaction response
     * @throws TransactionException                        if transaction is not found or not owned by user,
     *                                                     or if category type does not match transaction type
     * @throws CategoryException                           if category is invalid or not accessible
     * @throws PaymentModeException                        if payment mode is invalid or not accessible
     * @throws jakarta.persistence.EntityNotFoundException if current user is not found
     */
    TransactionResponse updateTransaction(
        UUID transactionId,
        UpdateTransactionRequest request
    );

    /**
     * Deletes a transaction belonging to the current user.
     * <p>Also removes the associated embedding.
     *
     * @param transactionId transaction identifier
     * @throws TransactionException    if transaction is not found or not owned by user
     * @throws EntityNotFoundException if current user is not found
     */
    void deleteTransaction(UUID transactionId);

}