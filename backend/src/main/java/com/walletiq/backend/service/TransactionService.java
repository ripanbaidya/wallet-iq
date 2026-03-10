package com.walletiq.backend.service;

import com.walletiq.backend.dto.transaction.CreateTransactionRequest;
import com.walletiq.backend.dto.transaction.TransactionFilterRequest;
import com.walletiq.backend.dto.transaction.TransactionResponse;
import com.walletiq.backend.dto.transaction.UpdateTransactionRequest;
import com.walletiq.backend.exception.TransactionException;
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
    TransactionResponse getTransactionById(UUID id);

    /**
     * Creates a transaction and triggers embedding as a side effect.
     */
    TransactionResponse createTransaction(CreateTransactionRequest request);

    /**
     * Updates a transaction and re-embeds it as a side effect.
     *
     * @throws TransactionException if not found or not owned by user.
     */
    TransactionResponse updateTransaction(UUID id, UpdateTransactionRequest request);

    /**
     * Deletes a transaction and removes its embedding as a side effect.
     *
     * @throws TransactionException if not found or not owned by user.
     */
    void deleteTransaction(UUID id);
}