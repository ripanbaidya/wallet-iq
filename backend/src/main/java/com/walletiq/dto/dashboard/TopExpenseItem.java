package com.walletiq.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Represents a top expense transaction for the selected period")
public record TopExpenseItem(

    @Schema(
        description = "Unique identifier of the transaction",
        example = "6c1d4b4f-4c9d-4c9a-9e4c-3e2c2a4b8a1f"
    )
    UUID id,

    @Schema(
        description = "Amount spent in the transaction",
        example = "1250.75"
    )
    BigDecimal amount,

    @Schema(
        description = "Category name associated with the expense (may be null if uncategorized)",
        example = "Food",
        nullable = true
    )
    String categoryName,

    @Schema(
        description = "Optional note attached to the transaction",
        example = "Dinner at restaurant"
    )
    String note,

    @Schema(
        description = "Date when the transaction occurred",
        example = "2026-03-14"
    )
    LocalDate date

) {
}