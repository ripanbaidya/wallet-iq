package com.walletiq.backend.dto.auth;

import com.walletiq.backend.entity.User;
import com.walletiq.backend.dto.user.AuthUserResponse;

public record AuthResponse(
    AuthUserResponse user,
    TokenResponse tokens
) {

    public static AuthResponse of(User user, TokenResponse tokens) {
        return new AuthResponse(AuthUserResponse.from(user), tokens);
    }
}