package com.sparta.sparta_eats.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

  @NotBlank(message = "사용자 ID는 필수입니다")
  private String userId;

  @NotBlank(message = "비밀번호는 필수입니다")
  private String password;
}