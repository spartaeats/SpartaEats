package com.sparta.sparta_eats.ai.infrastructure.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SuggestCommentRequest(String storeName,
                                    String nickname,
                                    Integer rate,
                                    String content,
                                    List<String> itemList) {
}
