package com.walletiq.service;

import com.walletiq.dto.mail.DailySummaryMailData;
import com.walletiq.dto.transaction.CreateTransactionRequest;
import com.walletiq.dto.transaction.TransactionFilterRequest;
import com.walletiq.dto.transaction.TransactionResponse;
import com.walletiq.dto.transaction.UpdateTransactionRequest;
import com.walletiq.entity.User;
import com.walletiq.exception.TransactionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {

    /**
     * Returns the current user's transactions, filtered by optional criteria.
     */
    Page<TransactionResponse> getAllTransactions(TransactionFilterRequest filter,
                                                 Pageable pageable);

    /**
     * Returns a single transaction owned by the current user.
     *
     * @throws TransactionException if not found or not owned by user.
     */
    TransactionResponse getTransactionById(UUID transactionId);

    /**
     * Creates a transaction and triggers embedding as a side effect.
     */
    TransactionResponse createTransaction(CreateTransactionRequest request);

    /**
     * Updates a transaction and re-embeds it as a side effect.
     *
     * @throws TransactionException if not found or not owned by user.
     */
    TransactionResponse updateTransaction(UUID transactionId, UpdateTransactionRequest request);

    /**
     * Deletes a transaction and removes its embedding as a side effect.
     *
     * @throws TransactionException if not found or not owned by user.
     */
    void deleteTransaction(UUID transactionId);

    DailySummaryMailData buildDailySummaryMailData(User user);
}