package com.walletiq.dto.notification;

import com.walletiq.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Represents a notification sent to the user")
public record NotificationResponse(

    @Schema(
        description = "Unique identifier of the notification",
        example = "9a6f8d3b-2c6f-4a6a-b5b2-0c9a3e5d7f11"
    )
    UUID id,

    @Schema(
        description = "Type of notification",
        example = "BUDGET_ALERT",
        allowableValues = {
            "LOGIN_ALERT",
            "BUDGET_ALERT",
            "RECURRING_TRANSACTION",
            "SAVINGS_GOAL",
            "SYSTEM"
        }
    )
    NotificationType type,

    @Schema(
        description = "Human-readable notification message",
        example = "You have reached 80% of your Food budget for April."
    )
    String message,

    @Schema(
        description = "Timestamp when the notification was created",
        example = "2026-03-16T10:45:30"
    )
    LocalDateTime createdAt

) {
}