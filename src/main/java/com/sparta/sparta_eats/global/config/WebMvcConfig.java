package com.sparta.sparta_eats.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final PaginationProperties paginationProperties;

    public WebMvcConfig(PaginationProperties paginationProperties) {
        this.paginationProperties = paginationProperties;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AllowedSizePageableResolver(paginationProperties));
    }
}