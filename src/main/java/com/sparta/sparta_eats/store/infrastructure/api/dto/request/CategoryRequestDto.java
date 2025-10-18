package com.sparta.sparta_eats.store.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {
    @NotBlank(message = "카테고리 코드(cate01)는 필수입니다.")
    @Size(max = 10, message = "카테고리 코드(cate01)는 최대 10자까지 가능합니다.")
    private String cate01;

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(max = 100, message = "카테고리 이름은 최대 100자까지 가능합니다.")
    private String name;
}
