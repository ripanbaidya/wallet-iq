package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Configuration properties for API-related settings.
 */
@ConfigurationProperties(prefix = "app.api")
public record ApplicationAPIProperties(

    Server servers
) {

    /**
     * Defines server endpoints for different environments.
     */
    public record Server(
        List<String> dev,
        List<String> prod
    ) {
    }
}
