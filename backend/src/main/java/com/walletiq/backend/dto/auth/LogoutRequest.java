package com.walletiq.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for user logout")
public record LogoutRequest(

    String refreshToken
) {
}
