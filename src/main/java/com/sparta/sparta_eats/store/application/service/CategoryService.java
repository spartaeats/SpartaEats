package com.sparta.sparta_eats.store.application.service;

import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.global.domain.exception.UnAuthorizedException;
import com.sparta.sparta_eats.store.domain.entity.Category;
import com.sparta.sparta_eats.store.domain.repository.CategoryRepository;
import com.sparta.sparta_eats.store.infrastructure.api.dto.request.CategoryRequestDto;
import com.sparta.sparta_eats.store.infrastructure.api.dto.response.CategoryResponseDto;
import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 등록 (관리자만 가능)
     */
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto, User currentUser) {
        if (currentUser.getRole() != UserRole.MASTER) {
            throw new UnAuthorizedException();
        }
        // 중복 카테고리 이름 체크
        categoryRepository.findByName(requestDto.getName()).ifPresent(category -> {
            throw new UnAuthorizedException();
        });

        Category category = Category.builder()
                .id(UUID.randomUUID()) // 새로운 UUID 생성
                .cate01(requestDto.getCate01())
                .name(requestDto.getName())
                .build();
        categoryRepository.save(category);
        return new CategoryResponseDto(category);
    }

    /**
     * 모든 카테고리 조회
     */
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 카테고리 조회
     */
    public CategoryResponseDto getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(UnAuthorizedException::new);
        return new CategoryResponseDto(category);
    }

    /**
     * 카테고리 수정 (관리자만 가능)
     */
    @Transactional
    public CategoryResponseDto updateCategory(UUID id, CategoryRequestDto requestDto, User currentUser) {
        if (currentUser.getRole() != UserRole.MASTER) {
            throw new UnAuthorizedException();
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        category.setCate01(requestDto.getCate01());
        category.setName(requestDto.getName());
        return new CategoryResponseDto(category);
    }

    /**
     * 카테고리 삭제 (관리자만 가능)
     */
    @Transactional
    public void deleteCategory(UUID id, User currentUser) {
        if (currentUser.getRole() != UserRole.MASTER) {
            throw new UnAuthorizedException();
        }

        categoryRepository.deleteById(id);
    }
}
