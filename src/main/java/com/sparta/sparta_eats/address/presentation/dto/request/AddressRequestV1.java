package com.sparta.sparta_eats.address.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddressRequestV1(
        @NotBlank(message = "{validation.address.name.not_blank}")
        @Size(max = 20, message = "{validation.address.name.size}")
        String name,

        @NotBlank(message = "{validation.address.addr_road.not_blank}")
        @Size(max = 255, message = "{validation.address.addr_road.size}")
        String addrRoad,

        @NotBlank(message = "{validation.address.addr_detail.not_blank}")
        @Size(max = 255, message = "{validation.address.addr_detail.size}")
        String addrDetail,

        @Size(max = 50, message = "{validation.address.memo.size}")
        String memo,

        @Size(max = 30, message = "{validation.address.direction.size}")
        String direction,

        @Size(max = 20, message = "{validation.address.entrance_password.size}")
        String entrancePassword
) {}
