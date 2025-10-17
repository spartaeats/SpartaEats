package com.sparta.sparta_eats.address.domain.dto;

import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddressSupplyDto(String addrRoad,
                               String addrDetail,
                               Coordinate coordinate,
                               String memo) {
}
