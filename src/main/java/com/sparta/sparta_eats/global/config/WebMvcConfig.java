package com.sparta.sparta_eats.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableConfigurationProperties(PaginationProperties.class)
public class WebMvcConfig implements WebMvcConfigurer {

    private final PaginationProperties paginationProperties;

    public WebMvcConfig(PaginationProperties paginationProperties) {
        this.paginationProperties = paginationProperties;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AllowedSizePageableResolver(paginationProperties));
    }

//    공통 인프라 문서(-한빈님 작성) 에 있는 내용인데 상의해야할 부분이라 주석으로 했습니다.
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//            .allowedOrigins("http://localhost:3000") // 프런트 도메인
//            .allowedMethods("GET","POST","PUT","PATCH","DELETE")
//            .allowCredentials(true);
//    }
}