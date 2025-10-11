package com.sparta.sparta_eats.user.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

  @Size(max = 100, message = "닉네임은 100자 이하여야 합니다")
  private String nickname;

  @Email(message = "올바른 이메일 형식이 아닙니다")
  private String email;

  @Pattern(regexp = "^\\d{10,11}$", message = "올바른 전화번호 형식이 아닙니다")
  private String phone;

  private Boolean isPublic;
}