package com.sparta.sparta_eats.store.infrastructure.api.dto.response;

import com.sparta.sparta_eats.store.domain.entity.Store;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class StoreResponseDto {
    private UUID id;
    private String name;
    private String address;
    private String addressDetail;
    private String image;
    private String phone;
    private LocalTime openHour;
    private LocalTime closeHour;
    private String statusDay;
    private String description;
    private Boolean status; // 0: Close, 1: Open
    private BigDecimal latitude;
    private BigDecimal longitude;

    private UUID categoryId; // 카테고리 ID
    private String categoryName; // 카테고리 이름

    private UUID ownerId; // 점주 ID
    private String ownerUsername; // 점주 사용자 이름

    public StoreResponseDto(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.address = store.getAddress();
        this.addressDetail = store.getAddressDetail();
        this.image = store.getImage();
        this.phone = store.getPhone();
        this.openHour = store.getOpenHour();
        this.closeHour = store.getCloseHour();
        this.statusDay = store.getStatusDay();
        this.description = store.getDescription();
        this.status = store.getStatus();
        this.latitude = store.getLatitude();
        this.longitude = store.getLongitude();

        if (store.getCategory() != null) {
            this.categoryId = store.getCategory().getId();
            this.categoryName = store.getCategory().getName();
        }

    }
}