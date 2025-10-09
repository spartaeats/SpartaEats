package com.sparta.sparta_eats.address.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoCoordinateResponse(List<Document> documents) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Document(String x, String y) { }
}
