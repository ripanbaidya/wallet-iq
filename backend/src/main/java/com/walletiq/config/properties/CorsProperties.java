package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Binds CORS-related security configuration from the `app.security.cors` prefix.
 * Used to control cross-origin request policies such as allowed origins, HTTP methods,
 * headers, and caching behavior.
 */
@ConfigurationProperties(prefix = "app.security.cors")
public record CorsProperties(

    boolean enabled,
    List<String> allowedOrigins,
    List<String> allowedMethods,
    List<String> allowedHeaders,
    List<String> exposedHeaders,
    boolean allowCredentials,
    long maxAge

) {
}