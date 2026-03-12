package com.walletiq.service.impl;

import com.walletiq.entity.Transaction;
import com.walletiq.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * No-op stub for EmbeddingService.
 * Used until Spring AI + PgVector is configured.
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