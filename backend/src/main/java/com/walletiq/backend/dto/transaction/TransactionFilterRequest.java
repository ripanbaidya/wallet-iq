package com.walletiq.backend.dto.transaction;

import com.walletiq.backend.enums.TxnType;

import java.time.LocalDate;

/**
 * Filter Record (used as @ModelAttribute in GET /api/transactions)
 */
public record TransactionFilterRequest(
    TxnType type,           // optional — filter by INCOME or EXPENSE
    String categoryId,      // optional — filter by category
    LocalDate dateFrom,     // optional — range start
    LocalDate dateTo        // optional — range end
) {
}
