package com.sparta.sparta_eats.global.infrastructure.api.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record FindStoresRequest(UUID storeId,
                                List<String> reviewList) {
}
