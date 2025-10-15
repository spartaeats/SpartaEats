package com.sparta.sparta_eats.ai.infrastructure.api.dto;

import lombok.Builder;

@Builder
public record SuggestItemDescriptionRequest(String name,
                                            String simpleDescription,
                                            String feature) {
}
