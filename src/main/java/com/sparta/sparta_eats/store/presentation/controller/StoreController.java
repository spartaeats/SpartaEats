package com.sparta.sparta_eats.store.presentation.controller;

import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import com.sparta.sparta_eats.store.infrastructure.api.dto.request.StoreRequestDto;
import com.sparta.sparta_eats.store.infrastructure.api.dto.response.StoreResponseDto;
import com.sparta.sparta_eats.store.application.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * 가게 등록 (점주만)
     */
    @PostMapping
    public ResponseEntity<StoreResponseDto> registerStore(
            @Valid @RequestBody StoreRequestDto requestDto, // StoreRequestDto 사용
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StoreResponseDto responseDto = storeService.registerStore(requestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 특정 가게 조회
     * 단일 조회를 위한 StoreResponseDto 반환
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponseDto> getStore(@PathVariable UUID storeId) {
        StoreResponseDto responseDto = storeService.getStore(storeId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 모든 가게 목록 조회 (페이징)
     * 목록 조회를 위한 Page<StoreResponseDto> 반환
     * 예: /api/stores?page=0&size=5&sort=name,asc
     */
    @GetMapping
    public ResponseEntity<Page<StoreResponseDto>> getAllStores(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<StoreResponseDto> responsePage = storeService.getAllStores(pageable);
        return ResponseEntity.ok(responsePage);
    }

    /**
     * 카테고리별 가게 목록 조회
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<StoreResponseDto>> getStoresByCategory(@PathVariable UUID categoryId) {
        List<StoreResponseDto> responseList = storeService.getStoresByCategory(categoryId);
        return ResponseEntity.ok(responseList);
    }

    /**
     * 가게 정보 수정 (점주만)
     */
    @PatchMapping("/{storeId}") // PATCH는 부분 업데이트를 의미
    public ResponseEntity<StoreResponseDto> updateStore(
            @PathVariable UUID storeId,
            @RequestBody StoreRequestDto requestDto, // StoreRequestDto 사용
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // PATCH 요청이므로 @Valid는 필수로 두지 않을 수 있지만,
        // DTO 내부의 개별 필드 유효성 (예: 전화번호 형식)은 여전히 검사되어야 하므로 추가.
        // 하지만 DTO 전체의 @NotBlank 같은 필수 필드 검사는 부분 업데이트 시에는 유연하게 적용해야 함.
        // 이 경우 서비스 계층에서 null 체크를 통해 처리하는 것이 바람직.
        StoreResponseDto responseDto = storeService.updateStore(storeId, requestDto, userDetails.getUser());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 가게 삭제 (점주만)
     */
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        storeService.deleteStore(storeId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    /**
     * 가게 영업 상태 변경 (점주만)
     */
    @PatchMapping("/{storeId}/status")
    public ResponseEntity<StoreResponseDto> changeStoreStatus(
            @PathVariable UUID storeId,
            @RequestParam("status") Boolean newStatus,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StoreResponseDto responseDto = storeService.changeStoreStatus(storeId, newStatus, userDetails.getUser());
        return ResponseEntity.ok(responseDto);
    }
}