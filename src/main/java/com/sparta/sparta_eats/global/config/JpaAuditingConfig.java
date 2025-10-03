package com.sparta.sparta_eats.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

//    QueryDSL 사용 할때 주석 해제해서 하시면 될 것 같습니다. 당장은 필요하지 않아서 주석처리했습니다.
//    @PersistenceContext
//    private EntityManager em;
//
//    @Bean
//    @Lazy
//    public JPAQueryFactory jpaQueryFactory() {
//        return new JPAQueryFactory(em);
//    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        // 나중에 SecurityContext에서 유저 정보 가져오도록 변경
        return () -> Optional.of("system-" + UUID.randomUUID());
    }
}