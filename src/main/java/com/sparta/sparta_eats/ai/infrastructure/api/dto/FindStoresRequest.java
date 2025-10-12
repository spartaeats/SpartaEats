package com.sparta.sparta_eats.ai.infrastructure.api.dto;

import com.sparta.sparta_eats.store.entity.Review;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record FindStoresRequest(UUID storeId,
                                List<Review> reviewList) {
}
