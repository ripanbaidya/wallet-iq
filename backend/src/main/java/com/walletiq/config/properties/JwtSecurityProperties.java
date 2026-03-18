package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds JWT-related security configuration from the `app.security.jwt` prefix.
 * Provides token metadata such as issuer, header configuration, and expiration
 * settings for access and refresh tokens.
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtSecurityProperties(

    String issuer,
    String header,
    String prefix,
    AccessToken accessToken,
    RefreshToken refreshToken
) {

    /**
     * Access token configuration.
     */
    public record AccessToken(
        long expiry
    ) {
    }

    /**
     * Refresh token configuration.
     */
    public record RefreshToken(
        long expiry
    ) {
    }
}