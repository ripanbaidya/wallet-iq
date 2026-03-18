package com.walletiq.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Schema(description = "Response payload representing the user's profile information")
@Builder
public record UserProfileResponse(

    @Schema(
        description = "Unique identifier of the user",
        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    UUID id,

    @Schema(
        description = "Full name of the user",
        example = "John Doe"
    )
    String fullName,

    @Schema(
        description = "Email address of the user",
        example = "john.doe@example.com"
    )
    String email,

    @Schema(
        description = "Indicates whether the user account is active",
        example = "true"
    )
    boolean active,

    @Schema(
        description = "Indicates whether the user's email address has been verified",
        example = "true"
    )
    boolean isEmailVerified
) {
}