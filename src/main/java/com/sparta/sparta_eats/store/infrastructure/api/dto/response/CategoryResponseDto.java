package com.sparta.sparta_eats.store.infrastructure.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {
    private UUID id;
    private String cate01;
    private String name;
}
