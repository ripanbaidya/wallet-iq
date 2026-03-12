package com.walletiq.backend.dto.mail;

import java.math.BigDecimal;

/**
 * Carrying all values needed to render the daily summary email template.
 */
public record DailySummaryMailData(
    String recipientName,
    String recipientEmail,
    String date,               // formatted: "Monday, 12 March 2026"
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal netBalance,
    int transactionCount
) {
}
