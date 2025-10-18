package com.sparta.sparta_eats.store.infrastructure.api.dto.response;

import com.sparta.sparta_eats.store.domain.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryResponseDto {
    private UUID id;
    private String cate01;
    private String name;

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.cate01 = category.getCate01();
        this.name = category.getName();
    }
}