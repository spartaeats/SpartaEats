package com.sparta.sparta_eats.address.infrastructure.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TmapRouteResponse(List<Feature> features) {

    public record Feature(Properties properties) {}

    // properties 객체 안에서 totalTime과 totalDistance만 선언
    // roadType, lineColor 등은 사용하지 않으므로 DTO에 아예 정의하지 않음
    public record Properties(@JsonProperty("totalTime") int totalTime) {
    }
}