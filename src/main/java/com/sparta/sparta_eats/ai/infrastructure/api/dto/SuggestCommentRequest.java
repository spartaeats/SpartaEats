package com.sparta.sparta_eats.ai.infrastructure.api.dto;

import java.util.List;

public record SuggestCommentRequest(String storeName,
                                    String nickname,
                                    Integer rate,
                                    String content,
                                    List<String> itemList) {
}
