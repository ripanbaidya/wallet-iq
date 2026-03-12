package com.walletiq.service;

import com.walletiq.entity.Transaction;

public interface EmbeddingService {

    /**
     * Converts the transaction to text, generates an embedding,
     * and stores it in the vector store.
     *
     * @return the vector document ID (embeddingId), or null if unavailable
     */
    String store(Transaction transaction);

    /**
     * Deletes the old vector and stores a fresh one for the updated transaction.
     *
     * @return the new vector document ID (embeddingId), or null if unavailable
     */
    String update(String embeddingId, Transaction transaction);

    /**
     * Deletes the vector document from the vector store.
     * Safe to call with a null embeddingId — will do nothing.
     */
    void delete(String embeddingId);
}