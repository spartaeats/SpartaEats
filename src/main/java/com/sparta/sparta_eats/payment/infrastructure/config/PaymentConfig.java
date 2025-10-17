
package com.sparta.sparta_eats.payment.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TossProperties.class)
public class PaymentConfig {
    // TossProperties 를 빈으로 등록하는 역할 (내용 비워도 OK)
}
