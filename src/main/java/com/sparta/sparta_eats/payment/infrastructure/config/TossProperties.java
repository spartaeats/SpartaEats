package com.sparta.sparta_eats.payment.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss")
public record TossProperties(
        String baseUrl,
        String secretKey,
        String clientKey,
        boolean mockEnabled
) { }
