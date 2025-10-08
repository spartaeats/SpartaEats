package com.sparta.sparta_eats.ai.infrastructure.api.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record FindStoresRequest(UUID storeId,
                                List<Object> reviewList) {
}
