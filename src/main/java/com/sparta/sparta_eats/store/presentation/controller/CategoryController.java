package com.sparta.sparta_eats.store.presentation.controller;

import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import com.sparta.sparta_eats.store.application.service.CategoryService;
import com.sparta.sparta_eats.store.infrastructure.api.dto.request.CategoryRequestDto;
import com.sparta.sparta_eats.store.infrastructure.api.dto.response.CategoryResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 등록 (관리자만)
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
            @Valid @RequestBody CategoryRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CategoryResponseDto responseDto = categoryService.createCategory(requestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 모든 카테고리 조회
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> responseList = categoryService.getAllCategories();
        return ResponseEntity.ok(responseList);
    }

    /**
     * 특정 카테고리 조회
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable UUID categoryId) {
        CategoryResponseDto responseDto = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 카테고리 수정 (관리자만)
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CategoryResponseDto responseDto = categoryService.updateCategory(categoryId, requestDto, userDetails.getUser());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 카테고리 삭제 (관리자만)
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable UUID categoryId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        categoryService.deleteCategory(categoryId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}
