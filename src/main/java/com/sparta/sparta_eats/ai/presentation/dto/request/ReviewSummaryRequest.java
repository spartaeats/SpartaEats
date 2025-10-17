package com.sparta.sparta_eats.ai.presentation.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record ReviewSummaryRequest(List<String> reviewList) {
}
