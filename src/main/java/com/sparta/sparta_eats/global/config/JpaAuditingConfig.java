package com.sparta.sparta_eats.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

@Configuration
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // 나중에 SecurityContext에서 유저 정보 가져오도록 변경
        return () -> Optional.of("system-" + UUID.randomUUID());
    }
}
