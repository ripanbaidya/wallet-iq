package com.walletiq.backend.dto.transaction;

import com.walletiq.backend.enums.TxnType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
    String id,
    BigDecimal amount,
    TxnType type,
    LocalDate date,
    String note,
    String categoryId,
    String categoryName,
    String paymentModeId,
    String paymentModeName,
    String embeddingId          // null until Spring AI is wired up
) {
}