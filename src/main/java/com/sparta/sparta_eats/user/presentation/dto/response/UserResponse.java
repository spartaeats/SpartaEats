package com.sparta.sparta_eats.user.presentation.dto.response;

import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.domain.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
  private String userId;
  private String nickname;
  private String email;
  private String phone;
  private UserRole role;
  private Boolean isPublic;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime lastLoginAt;

  public static UserResponse from(User user) {
    return UserResponse.builder()
        .userId(user.getUserId())
        .nickname(user.getNickname())
        .email(user.getEmail())
        .phone(user.getPhone())
        .role(user.getRole())
        .isPublic(user.getIsPublic())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .lastLoginAt(user.getLastLoginAt())
        .build();
  }
}