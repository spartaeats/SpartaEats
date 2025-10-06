package com.sparta.sparta_eats.address.domain;

import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LocationInfo {
    private String address;
    private String name;
    private Coordinate coordinate;
}
