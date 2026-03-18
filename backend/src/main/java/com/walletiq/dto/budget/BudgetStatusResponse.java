package com.walletiq.dto.budget;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Builder
@Schema(description = "Represents the current spending status of a category budget")
public record BudgetStatusResponse(

    @Schema(
        description = "Unique identifier of the budget",
        example = "7c1d3a4e-3d2e-4c7b-a7e5-8a0a9d7b5c21"
    )
    UUID budgetId,

    @Schema(
        description = "Name of the category associated with the budget",
        example = "Food"
    )
    String categoryName,

    @Schema(
        description = "Budget month in format yyyy-MM",
        example = "2026-04",
        type = "string"
    )
    YearMonth month,

    @Schema(
        description = "Maximum allowed spending for the category in the specified month",
        example = "5000.00"
    )
    BigDecimal limitAmount,

    @Schema(
        description = "Total amount spent so far within the specified month",
        example = "3200.50"
    )
    BigDecimal spentAmount,

    @Schema(
        description = "Remaining amount available before reaching the budget limit",
        example = "1799.50"
    )
    BigDecimal remainingAmount,

    @Schema(
        description = "Percentage of the budget that has been used (can exceed 100 if limit is breached)",
        example = "64.01"
    )
    double usagePercentage,

    @Schema(
        description = "Indicates whether the configured alert threshold has been exceeded",
        example = "false"
    )
    boolean thresholdBreached,

    @Schema(
        description = "Indicates whether the budget spending limit has been exceeded",
        example = "false"
    )
    boolean limitBreached

) {
}