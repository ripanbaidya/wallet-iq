package com.walletiq.dto.savingsgoal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request payload to create a new savings goal")
public record SavingsGoalRequest(

    @Schema(
        description = "Title of the savings goal",
        example = "Buy MacBook Pro",
        maxLength = 100,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Title is required")
    @Size(max = 100)
    String title,

    @Schema(
        description = "Target amount to save",
        example = "150000.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
    BigDecimal targetAmount,

    @Schema(
        description = "Deadline by which the target amount should be saved",
        example = "2026-12-31",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    LocalDate deadline,

    @Schema(
        description = "Optional note for additional context about the savings goal",
        example = "Saving monthly for a new laptop"
    )
    String note

) {
}