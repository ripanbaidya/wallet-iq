package com.walletiq.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload returned after successful authentication or token refresh.
 */
@Schema(description = "Response containing authentication tokens")
public record TokenResponse(

    @Schema(
        description = "JWT access token used to authorize API requests",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.accessTokenExample"
    )
    String accessToken,

    @Schema(
        description = "Refresh token used to obtain a new access token when it expires",
        example = "3d4f6c98-2a1b-4c1e-9a5d-9b9b0c4e2a11"
    )
    String refreshToken,

    @Schema(
        description = "Token type used in the Authorization header",
        example = "Bearer"
    )
    String tokenType,

    @Schema(
        description = "Access token expiration time in seconds",
        example = "900"
    )
    Long expiresIn

) {

    /**
     * Factory method to construct a TokenResponse with the default token type (Bearer).
     *
     * @param accessToken  generated JWT access token
     * @param refreshToken generated refresh token
     * @param expiresIn    access token expiration time in seconds
     * @return TokenResponse instance
     */
    public static TokenResponse of(String accessToken, String refreshToken, Long expiresIn) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}