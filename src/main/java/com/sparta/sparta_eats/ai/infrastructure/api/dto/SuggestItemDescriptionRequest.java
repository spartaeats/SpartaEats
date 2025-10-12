package com.sparta.sparta_eats.ai.infrastructure.api.dto;

public record SuggestItemDescriptionRequest(String name,
                                            String simpleDescription,
                                            String feature) {
}
