package com.walletiq.backend.dto.auth;

public record RefreshTokenRequest(
    String refreshToken
) {
}