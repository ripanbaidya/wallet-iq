package com.walletiq.backend.service.impl;

import com.walletiq.backend.entity.Transaction;
import com.walletiq.backend.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * No-op stub for EmbeddingService.
 * Used until Spring AI + PgVector is configured.
 * Replace this with PgVectorEmbeddingServiceImpl when ready.
 * TransactionServiceImpl does not need to change at all.
 */
@Slf4j
@Service
public class NoOpEmbeddingServiceImpl implements EmbeddingService {

    @Override
    public String store(Transaction transaction) {
        log.debug("NoOp embedding store called for transaction id={}", transaction.getId());
        return null;
    }

    @Override
    public String update(String embeddingId, Transaction transaction) {
        log.debug("NoOp embedding update called for transaction id={}", transaction.getId());
        return null;
    }

    @Override
    public void delete(String embeddingId) {
        log.debug("NoOp embedding delete called for embeddingId={}", embeddingId);
    }
}