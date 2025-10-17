package com.sparta.sparta_eats.store.domain.repository;

import com.sparta.sparta_eats.store.domain.entity.Category;

import java.util.Optional;

public interface CategoryRepository {
    // 카테고리 이름으로 찾기
    Optional<Category> findByName(String name);

    // cate01 (대분류)으로 찾기
    Optional<Category> findByCate01(String cate01);

    // 카테고리 이름 존재 여부 확인
    boolean existsByName(String name);

    // cate01 (대분류) 존재 여부 확인
    boolean existsByCate01(String cate01);
}
