package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provides centralized access to application information such as versioning,
 * licensing, support contact, and developer details, typically used in API
 * metadata, documentation, and public info endpoints.
 */
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

    /**
     * License information associated with the application.
     */
    public record License(
        String name,
        String url
    ) {
    }

    /**
     * Support contact details for users of the application.
     */
    public record Support(
        String email,
        String workingHours
    ) {
    }

    /**
     * Social or public repository links related to the project.
     */
    public record Social(
        String github
    ) {
    }

    /**
     * Developer or maintainer contact information.
     */
    public record Developer(
        String name,
        String email
    ) {
    }
}