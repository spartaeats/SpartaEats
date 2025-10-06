package com.sparta.sparta_eats.address.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coordinate {
    @Column(name = "addr_lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal addrLat;
    @Column(name = "addr_lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal addrLng;

    @Builder
    public Coordinate(BigDecimal addrLat, BigDecimal addrLng) {
        this.addrLat = addrLat;
        this.addrLng = addrLng;
    }
}
