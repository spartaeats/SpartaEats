package com.sparta.sparta_eats.global.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiResponse(List<Candidates> candidates) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidates(Content content) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(List<Text> parts) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Text(String text) { }
}
