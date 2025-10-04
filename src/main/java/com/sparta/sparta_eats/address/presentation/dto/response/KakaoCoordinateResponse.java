package com.sparta.sparta_eats.address.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoCoordinateResponse(List<Document> documents) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Document {
        private String y;
        private String x;
    }
}
