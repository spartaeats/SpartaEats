package com.sparta.sparta_eats.store.infrastructure.api.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StoreRequestDto {

    private String name;

    private String address;
    private String addressDetail;
    private String image;
    private String phone;
    private String openHour;
    private String closeHour;
    private String statusDay; // 영업 요일
    private String description;
    private Boolean status; // true: Open, false: Close
    private Double latitude;
    private Double longitude;
    private UUID categoryId;
    private UUID ownerId;
}
