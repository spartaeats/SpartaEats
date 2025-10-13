package com.sparta.sparta_eats.global.infrastructure.api.dto;

import lombok.Builder;

@Builder
public record SuggestItemDescriptionRequest(String name,
                                            String simpleDescription,
                                            String feature) {
}
