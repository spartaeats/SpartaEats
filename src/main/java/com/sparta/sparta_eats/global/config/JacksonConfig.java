package com.sparta.sparta_eats.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // LocalDateTime 같은 Java 8 Date/Time API 지원
        mapper.registerModule(new JavaTimeModule());

        // timestamp 숫자 대신 ISO-8601 문자열로 출력
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // null 값인 필드는 아예 응답 JSON에서 제외하고 싶으면 (선택)
        // mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
