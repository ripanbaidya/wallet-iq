package com.walletiq.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for refreshing an access token using a refresh token")
public record RefreshTokenRequest(

    @Schema(
        description = "Valid refresh token issued during login",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Refresh token is required")
    @Size(max = 500, message = "Refresh token length must not exceed 500 characters")
    String refreshToken

) {
}