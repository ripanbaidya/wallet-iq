package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds RSA cryptographic configuration from the `app.security.rsa` prefix.
 * Provides the algorithm and key locations used for JWT signing and verification.
 */
@ConfigurationProperties(prefix = "app.security.rsa")
public record RSAProperties(

    String algorithm,
    String privateKeyPath,
    String publicKeyPath
) {
}