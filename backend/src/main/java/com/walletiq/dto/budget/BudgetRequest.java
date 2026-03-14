package com.walletiq.dto.budget;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Schema(description = "Request payload for creating or updating a monthly category budget")
public record BudgetRequest(

    @Schema(
        description = "Category ID for which the budget is set",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Category ID is required")
    UUID categoryId,

    @Schema(
        description = "Budget month in format yyyy-MM",
        example = "2026-04",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Month is required (yyyy-MM)")
    YearMonth month,

    @Schema(
        description = "Maximum amount allowed to spend for the category in the specified month",
        example = "5000.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @DecimalMin("1.00")
    @Digits(integer = 10, fraction = 2)
    BigDecimal limitAmount,

    @Schema(
        description = "Alert threshold percentage when spending approaches the limit",
        example = "80",
        defaultValue = "80",
        minimum = "1",
        maximum = "100"
    )
    @Min(1)
    @Max(100)
    int alertThreshold

) {

    public BudgetRequest {
        if (alertThreshold == 0) {
            alertThreshold = 80;
        }
    }
}