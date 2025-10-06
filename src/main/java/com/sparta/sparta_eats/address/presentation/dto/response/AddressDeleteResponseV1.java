package com.sparta.sparta_eats.address.presentation.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AddressDeleteResponseV1(String name,
                                      String addRoad,
                                      String deletedBy,
                                      LocalDateTime deletedAt) {
}
