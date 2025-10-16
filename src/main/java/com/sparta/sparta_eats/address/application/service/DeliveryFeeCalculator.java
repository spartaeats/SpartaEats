package com.sparta.sparta_eats.address.application.service;

import com.sparta.sparta_eats.address.domain.LocationInfo;
import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.infrastructure.api.TmapApiClient;
import com.sparta.sparta_eats.address.presentation.dto.response.DistanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryFeeCalculator {
    private final TmapApiClient tmapApiClient;

    public DistanceResponse getDistanceInfo(LocationInfo start, LocationInfo target) {
        // TODO store 연동 되면 store charge 정보 가져와야함
        // 이후에 요금 계산 정책이 복잡해 질 시에 계산 로직을 클래스로 분리
        int charge = 0;
        int distance = tmapApiClient.getDistance(start.getCoordinate(), target.getCoordinate());
        int time = tmapApiClient.getTime(start.getCoordinate(), target.getCoordinate());
        charge += (distance - 2000) / 1000 * 100;

        return DistanceResponse.builder()
                .start(start)
                .target(target)
                .charge(charge)
                .distance(distance)
                .time(time)
                .build();
    }
}
