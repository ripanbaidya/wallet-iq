package com.walletiq.backend.payload.response;

import com.walletiq.backend.entity.User;

public record AuthResponse(
    AuthUserResponse user,
    TokenResponse tokens
) {

    public static AuthResponse of(User user, TokenResponse tokens) {
        return new AuthResponse(AuthUserResponse.from(user), tokens);
    }
}