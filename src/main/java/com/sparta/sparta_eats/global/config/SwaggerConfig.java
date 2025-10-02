package com.sparta.sparta_eats.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sparta Eats API")
                        .description("스파르타 이츠 프로젝트 API 명세서")
                        .version("v1"))
                .servers(List.of(
                        new Server().url("/v1").description("API Base URL")
                ));
    }
}
