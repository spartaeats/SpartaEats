package com.sparta.sparta_eats.address.presentation.dto.response;

import com.sparta.sparta_eats.address.domain.LocationInfo;
import lombok.Builder;

@Builder
public record DistanceResponse(LocationInfo start,
                               LocationInfo target,
                               Integer distance,
                               Integer charge,
                               Integer time) {
}
