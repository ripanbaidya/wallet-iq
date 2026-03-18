package com.walletiq.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload used for generating a secure password hash.
 * This endpoint is typically used for internal/debug purposes or when verifying
 * hashing configuration. The password provided here will NOT be stored directly.
 */
@Schema(description = "Request payload for generating a password hash")
public record PasswordHashRequest(

    @Schema(
        description = "Plain text password that will be hashed using the configured password encoder",
        example = "MySecurePassword123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Password is required")
    @Size(
        min = 8,
        max = 100,
        message = "Password must be between 8 and 100 characters"
    )
    String password

) {
}