package com.walletiq.dto.auth;

import com.walletiq.entity.User;
import com.walletiq.dto.user.AuthUserResponse;

public record AuthResponse(
    AuthUserResponse user,
    TokenResponse tokens
) {

    public static AuthResponse of(User user, TokenResponse tokens) {
        return new AuthResponse(AuthUserResponse.from(user), tokens);
    }
}