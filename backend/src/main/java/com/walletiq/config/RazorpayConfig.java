package com.walletiq.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.walletiq.config.properties.RazorpayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RazorpayConfig {

    private final RazorpayProperties razorpayProperties;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(
            razorpayProperties.keyId(),
            razorpayProperties.keySecret()
        );
    }
}
