package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(

    String name,
    String version,
    String buildNumber,
    String copyright,
    License license,
    Support support,
    Social social,
    Developer developer
) {

    public record License(
        String name,
        String url
    ) {
    }

    public record Support(
        String email,
        String workingHours
    ) {
    }

    public record Social(
        String github
    ) {
    }

    public record Developer(
        String name,
        String email
    ) {
    }
}
