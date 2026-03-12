package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.api")
public record ApplicationAPIProperties(

    Server servers
) {

    public record Server(
        List<String> dev,
        List<String> prod
    ) {
    }
}
