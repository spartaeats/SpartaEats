package com.sparta.sparta_eats.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.pagination")
public record PaginationProperties(
        int defaultSize,
        List<Integer> allowedSizes,
        boolean oneIndexed
) { }
