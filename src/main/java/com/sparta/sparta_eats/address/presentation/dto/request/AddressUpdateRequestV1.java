package com.sparta.sparta_eats.address.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AddressUpdateRequestV1(@NotNull UUID id,
                                     @NotBlank @Size(max = 20) String name,
                                     @NotBlank @Size(max = 255) String addrRoad,
                                     @NotBlank @Size(max = 255) String addrDetail,
                                     @Size(max = 50) String memo,
                                     @Size(max = 30) String direction,
                                     @Size(max = 20) String entrancePassword ) {
}
