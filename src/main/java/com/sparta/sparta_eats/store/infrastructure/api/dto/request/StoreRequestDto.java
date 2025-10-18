package com.sparta.sparta_eats.store.infrastructure.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class StoreRequestDto {
    @Size(max = 100, message = "가게 이름은 최대 100자까지 가능합니다.")
    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @Size(max = 255, message = "상세 주소는 최대 255자까지 가능합니다.")
    private String addressDetail;

    @Size(max = 500, message = "이미지 URL은 최대 500자까지 가능합니다.")
    private String image;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "유효하지 않은 전화번호 형식입니다.")
    private String phone;

    private LocalTime openHour;
    private LocalTime closeHour;

    @Size(max = 10, message = "영업 상태 요일은 최대 10자까지 가능합니다.")
    private String statusDay; // 요일별 영업 상태 (예: "Mon-Fri")

    @Size(max = 1000, message = "가게 설명은 최대 1000자까지 가능합니다.")
    private String description;

    // 등록 시에는 필수로 받고, 수정 시에는 변경될 경우에만 보냄
    private Boolean status; // 0: Close, 1: Open

    // 경위도 정보 (선택적)
    // BigDecimal의 정밀도와 스케일 제약은 JPA Entity에서 정의되었으므로 여기서는 최소한의 유효성 검사만
    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private UUID categoryId; // Category 엔티티의 ID를 받음
}
