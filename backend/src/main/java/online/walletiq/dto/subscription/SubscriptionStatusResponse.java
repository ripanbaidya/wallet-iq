package online.walletiq.dto.subscription;

import online.walletiq.enums.SubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
    name = "SubscriptionStatusResponse",
    description = "Represents the current subscription state of the user"
)
public record SubscriptionStatusResponse(

    @Schema(
        description = "Indicates whether the subscription is currently active",
        example = "true"
    )
    boolean isActive,

    @Schema(
        description = "UTC timestamp when the subscription expires. Null if no active subscription",
        example = "2026-04-15T10:15:30Z",
        nullable = true
    )
    Instant expiresAt,

    @Schema(
        description = "Detailed subscription status",
        example = "ACTIVE",
        allowableValues = {"PENDING", "ACTIVE", "EXPIRED", "FAILED"}
    )
    SubscriptionStatus status

) {
}