package com.sparta.sparta_eats.ai.config;

import com.sparta.sparta_eats.ai.domain.exception.GeminiOverloadException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(handleErrorResponse());
    }

    private ExchangeFilterFunction handleErrorResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(error -> {
                            GeminiOverloadException exception = switch (clientResponse.statusCode().value()) {
                                case 429 -> new GeminiOverloadException("Gemini에 너무 많은 요청이 발생했습니다.", HttpStatus.TOO_MANY_REQUESTS);
                                // TODO 429 이외에 발생할 수 있는 에러 처리
                                default -> new GeminiOverloadException("Gemini API 오류", HttpStatus.INTERNAL_SERVER_ERROR);
                            };
                            return Mono.error(exception);
                        });
            } else {
                return Mono.just(clientResponse);
            }
        });
    }
}
