package com.walletiq.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for generating a password hash")
public record PasswordHashRequest(

    @Schema(
        description = "Plain text password that needs to be hashed",
        example = "MySecurePassword123!"
    )
    @NotBlank(message = "Password must not be blank")
    String password

) {
}