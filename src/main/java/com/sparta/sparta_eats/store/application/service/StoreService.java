package com.sparta.sparta_eats.store.application.service;

import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.global.domain.exception.UnAuthorizedException;
import com.sparta.sparta_eats.store.domain.entity.Category;
import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.store.domain.repository.CategoryRepository;
import com.sparta.sparta_eats.store.domain.repository.StoreRepository;
import com.sparta.sparta_eats.store.infrastructure.api.dto.request.StoreRequestDto;
import com.sparta.sparta_eats.store.infrastructure.api.dto.response.StoreResponseDto;
import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 가게 등록 (점주만 가능)
     */
    @Transactional
    public StoreResponseDto registerStore(StoreRequestDto requestDto, User currentUser) {
        // 현재 로그인한 사용자가 점주인지 확인
        if (currentUser.getRole().ordinal() <  UserRole.OWNER.ordinal()) {
            // 사용자 권한 관련 예외처리는 SecurityLayer에서 처리할 수도 있습니다.
            throw new UnAuthorizedException("일반 고객은 매장 등록 서비스를 이용할 수 없습니다.");
        }

        // 카테고리 존재 여부 확인
        Category category = (Category) categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(NotFoundException::new);

        Store store = Store.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .addressDetail(requestDto.getAddressDetail())
                .image(requestDto.getImage())
                .phone(requestDto.getPhone())
                .openHour(requestDto.getOpenHour())
                .closeHour(requestDto.getCloseHour())
                .statusDay(requestDto.getStatusDay()) // 추가
                .description(requestDto.getDescription())
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : false) // 등록 시 명시하지 않으면 false(휴업)
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .category(category)
                .owner(currentUser)
                .build();

        storeRepository.save(store);
        return new StoreResponseDto(store);
    }

    /**
     * 특정 가게 조회
     */
    public StoreResponseDto getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(NotFoundException::new);
        return new StoreResponseDto(store);
    }

    /**
     * 모든 가게 목록 조회 (페이징)
     */
    public Page<StoreResponseDto> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable)
                .map(StoreResponseDto::new);
    }

    /**
     * 카테고리별 가게 목록 조회
     */
    public List<StoreResponseDto> getStoresByCategory(UUID categoryId) {
        List<Store> stores = storeRepository.findByCategory_Id(categoryId);
        return stores.stream()
                .map(StoreResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 가게 정보 수정 (해당 가게의 점주만 가능)
     */
    @Transactional
    public StoreResponseDto updateStore(UUID storeId, StoreRequestDto requestDto, User currentUser) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(NotFoundException::new);

        // 해당 가게의 점주인지 확인
        if (!store.getOwner().getUserId().equals(currentUser.getUserId())) {
            throw new UnAuthorizedException();
        }

        // 카테고리 변경 시
        Category category = store.getCategory(); // 기존 카테고리 유지
        if (requestDto.getCategoryId() != null && !requestDto.getCategoryId().equals(category.getId())) {
            category = (Category) categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(NotFoundException::new);
        }


        if (requestDto.getName() != null) store.setName(requestDto.getName());
        if (requestDto.getAddress() != null) store.setAddress(requestDto.getAddress());
        if (requestDto.getAddressDetail() != null) store.setAddressDetail(requestDto.getAddressDetail());
        if (requestDto.getImage() != null) store.setImage(requestDto.getImage());
        if (requestDto.getPhone() != null) store.setPhone(requestDto.getPhone());
        if (requestDto.getOpenHour() != null) store.setOpenHour(requestDto.getOpenHour());
        if (requestDto.getCloseHour() != null) store.setCloseHour(requestDto.getCloseHour());
        if (requestDto.getStatusDay() != null) store.setStatusDay(requestDto.getStatusDay()); // 추가
        if (requestDto.getDescription() != null) store.setDescription(requestDto.getDescription());
        if (requestDto.getStatus() != null) store.setStatus(requestDto.getStatus());
        if (requestDto.getLatitude() != null) store.setLatitude(requestDto.getLatitude());
        if (requestDto.getLongitude() != null) store.setLongitude(requestDto.getLongitude());
        if (category != null && !category.equals(store.getCategory())) { // 기존과 다르면 업데이트
            store.setCategory(category);
        }

        // save를 호출하지 않아도 더티 체킹에 의해 업데이트됨
        return new StoreResponseDto(store);
    }

    /**
     * 가게 삭제 (해당 가게의 점주만 가능)
     */
    @Transactional
    public void deleteStore(UUID storeId, User currentUser) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(NotFoundException::new);

        if (!store.getOwner().getUserId().equals(currentUser.getUserId())) {
            throw new UnAuthorizedException();
        }

        storeRepository.delete(store);
    }

    // 가게 영업 상태 변경 메서드는 기존과 동일하게 유지
    @Transactional
    public StoreResponseDto changeStoreStatus(UUID storeId, Boolean newStatus, User currentUser) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(NotFoundException::new);

        if (!store.getOwner().getUserId().equals(currentUser.getUserId())) {
            throw new UnAuthorizedException();
        }

        store.setStatus(newStatus);
        return new StoreResponseDto(store);
    }
}