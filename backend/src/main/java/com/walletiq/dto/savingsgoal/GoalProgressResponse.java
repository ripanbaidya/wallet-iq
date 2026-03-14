package com.walletiq.dto.savingsgoal;

import com.walletiq.enums.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Schema(description = "Response payload representing the progress of a savings goal")
public record GoalProgressResponse(

    @Schema(
        description = "Unique identifier of the savings goal",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    UUID id,

    @Schema(
        description = "Title of the savings goal",
        example = "Buy MacBook Pro"
    )
    String title,

    @Schema(
        description = "Target amount to save",
        example = "150000.00"
    )
    BigDecimal targetAmount,

    @Schema(
        description = "Total amount already saved",
        example = "50000.00"
    )
    BigDecimal savedAmount,

    @Schema(
        description = "Remaining amount required to reach the goal",
        example = "100000.00"
    )
    BigDecimal remainingAmount,

    @Schema(
        description = "Progress percentage of the savings goal (0–100)",
        example = "33.33"
    )
    double progressPercentage,

    @Schema(
        description = "Number of days remaining until the deadline (negative if overdue)",
        example = "45"
    )
    long daysRemaining,

    @Schema(
        description = "Current status of the goal",
        example = "IN_PROGRESS"
    )
    GoalStatus status,

    @Schema(
        description = "Deadline date of the savings goal",
        example = "2026-12-31"
    )
    LocalDate deadline

) {
}