package com.walletiq.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;

@Builder
@Schema(name = "AppInfoResponse", description = "Application metadata and public service information")
public record AppInfoResponse(

    @Schema(description = "Application name", example = "WalletIQ")
    String name,

    @Schema(description = "Current application version", example = "1.0.0")
    String version,

    @Schema(description = "Build identifier of the running application", example = "2026.03.15")
    String buildNumber,

    @Schema(description = "Current server time in UTC", example = "2026-03-16T10:15:30Z")
    Instant serverTime,

    @Schema(description = "Copyright information", example = "© 2026 WalletIQ")
    String copyright,

    @Schema(description = "License information for the application")
    License license,

    @Schema(description = "Support contact information")
    Support support,

    @Schema(description = "Official project links")
    Social social

) {

    /**
     * Application license information.
     */
    @Schema(name = "AppLicense")
    public record License(

        @Schema(description = "License name", example = "Apache 2.0")
        String name,

        @Schema(description = "URL to the license documentation", example = "https://www.apache.org/licenses/LICENSE-2.0")
        String url
    ) {
    }

    /**
     * Support contact details for users.
     */
    @Schema(name = "AppSupport")
    public record Support(

        @Schema(description = "Support email address", example = "support@walletiq.com")
        String email,

        @Schema(description = "Support availability hours", example = "Mon–Fri 09:00–18:00 IST")
        String workingHours
    ) {
    }

    /**
     * Public repository or social links related to the project.
     */
    @Schema(name = "AppSocial")
    public record Social(

        @Schema(description = "GitHub repository URL", example = "https://github.com/ripanbaidya/wallet-iq")
        String github
    ) {
    }

}