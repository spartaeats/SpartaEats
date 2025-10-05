package com.sparta.sparta_eats.user.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
  private String message;
  private String token;
  private UserResponse user;

  public static AuthResponse of(String message, String token, UserResponse user) {
    return AuthResponse.builder()
        .message(message)
        .token(token)
        .user(user)
        .build();
  }
}