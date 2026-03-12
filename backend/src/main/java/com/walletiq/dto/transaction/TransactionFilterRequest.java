package com.walletiq.dto.transaction;

import com.walletiq.enums.TxnType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Request object used to filter transactions by type, category, and date range")
public record TransactionFilterRequest(

    @Schema(
        description = "Filter transactions by type",
        example = "EXPENSE",
        allowableValues = {"INCOME", "EXPENSE"},
        nullable = true
    )
    TxnType type,

    @Schema(
        description = "Category ID to filter transactions",
        nullable = true
    )
    String categoryId,

    @Schema(
        description = "Start date for transaction filtering (inclusive)",
        example = "2026-01-01",
        type = "string",
        format = "date",
        nullable = true
    )
    LocalDate dateFrom,

    @Schema(
        description = "End date for transaction filtering (inclusive)",
        example = "2026-01-31",
        type = "string",
        format = "date",
        nullable = true
    )
    LocalDate dateTo

) {
}