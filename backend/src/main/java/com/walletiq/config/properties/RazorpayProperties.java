package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Razorpay.
 */
@ConfigurationProperties(prefix = "app.razorpay")
public record RazorpayProperties(

    String keyId,
    String keySecret,
    String currency,
    int subscriptionAmount,
    int subscriptionDays

) {
}
