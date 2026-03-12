package com.walletiq.config;

import com.walletiq.config.properties.ApplicationAPIProperties;
import com.walletiq.config.properties.ApplicationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

import static com.walletiq.constant.OpenAPIConstant.API_DESCRIPTION;
import static com.walletiq.constant.OpenAPIConstant.AUTH_DESCRIPTION;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

    private static final String BEARER_AUTH = "BearerAuth";

    private final ApplicationAPIProperties applicationAPIProperties;
    private final ApplicationProperties applicationProperties;

    @Bean
    @Profile("!prod")
    public OpenAPI nonProductionOpenAPI() {
        return createOpenAPI(
            getServers(applicationAPIProperties.servers().dev(), "Development Environment"),
            true
        );
    }

    @Bean
    @Profile("prod")
    public OpenAPI productionOpenAPI() {
        return createOpenAPI(
            getServers(applicationAPIProperties.servers().prod(), "Production Environment"),
            false
        );
    }

    private OpenAPI createOpenAPI(List<Server> servers, boolean isDevelopment) {
        String description = API_DESCRIPTION.formatted(AUTH_DESCRIPTION);

        Info info = new Info()
            .title(applicationProperties.name())
            .version(applicationProperties.version())
            .description(description)
            .contact(new Contact()
                .name(applicationProperties.name())
                .email(applicationProperties.support().email()))
            .license(new License()
                .name(applicationProperties.license().name())
                .url(applicationProperties.license().url()));

        return new OpenAPI()
            .info(info)
            .servers(servers)
            .tags(createApiTags(isDevelopment))
            .components(createSecurityComponents())
            .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    /**
     * JWT Security configuration
     */
    private Components createSecurityComponents() {
        return new Components()
            .addSecuritySchemes(BEARER_AUTH,
                new SecurityScheme()
                    .name("Authorization")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Bearer token authentication"));
    }

    /**
     * API Tags for endpoint grouping
     */
    private List<Tag> createApiTags(boolean isDevelopment) {
        List<Tag> tags = new ArrayList<>(List.of(
            createTag("Authentication", "Authentication",
                "Login, registration, and token management.", true),

            createTag("Users", "User Management",
                "User account and profile management.", true),

            createTag("Admin", "Administration",
                "Administrative endpoints requiring ADMIN privileges.", false)
        ));

        if (isDevelopment) {
            tags.add(createTag("Test", "Test Endpoints",
                "Internal testing endpoints (development only).", false));
        }

        return tags;
    }

    /**
     * Tag builder
     */
    private Tag createTag(String name, String displayName, String description, boolean isPublic) {
        Tag tag = new Tag().name(name).description(description);

        tag.addExtension("x-displayName", displayName);
        tag.addExtension("x-public", isPublic ? "yes" : "no");

        return tag;
    }

    /**
     * Server configuration
     */
    private List<Server> getServers(List<String> urls, String description) {
        return urls.stream()
            .map(url -> new Server()
                .url(url)
                .description(description))
            .toList();
    }
}