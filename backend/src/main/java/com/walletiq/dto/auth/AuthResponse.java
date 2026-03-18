package com.walletiq.dto.auth;

import com.walletiq.entity.User;
import com.walletiq.dto.user.AuthUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "AuthResponse",
    description = "Authentication response containing user details and issued tokens"
)
public record AuthResponse(

    @Schema(description = "Authenticated user information")
    AuthUserResponse user,

    @Schema(description = "Issued access and refresh tokens")
    TokenResponse tokens

) {

    /**
     * Factory method to construct an AuthResponse from a User entity
     * and generated token payload.
     */
    public static AuthResponse of(User user, TokenResponse tokens) {
        return new AuthResponse(AuthUserResponse.from(user), tokens);
    }
}