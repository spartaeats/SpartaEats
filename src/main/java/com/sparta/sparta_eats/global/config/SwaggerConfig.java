package com.sparta.sparta_eats.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    // JWT 보안 스키마 이름
    String jwtSchemeName = "bearerAuth";

    // SecurityRequirement 추가 (전역으로 적용)
    SecurityRequirement securityRequirement = new SecurityRequirement()
        .addList(jwtSchemeName);

    // SecurityScheme 정의
    Components components = new Components()
        .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
            .name(jwtSchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT 토큰을 입력해주세요. 'Bearer ' 접두사는 자동으로 추가됩니다."));

    return new OpenAPI()
        .info(new Info()
            .title("Sparta Eats API")
            .description("스파르타 이츠 프로젝트 API 명세서")
            .version("v1"))
        .servers(List.of(
            new Server().url("/").description("API Base URL")
        ))
        .addSecurityItem(securityRequirement)
        .components(components);
  }
}