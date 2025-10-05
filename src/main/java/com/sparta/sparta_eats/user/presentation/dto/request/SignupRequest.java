package com.sparta.sparta_eats.user.presentation.dto.request;

import com.sparta.sparta_eats.user.domain.entity.UserRole;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

  @NotBlank(message = "사용자 ID는 필수입니다")
  @Pattern(regexp = "^[a-z0-9]{4,10}$",
      message = "사용자 ID는 4-10자의 소문자와 숫자만 가능합니다")
  private String userId;

  @NotBlank(message = "닉네임은 필수입니다")
  @Size(max = 100, message = "닉네임은 100자 이하여야 합니다")
  private String nickname;

  @NotBlank(message = "이메일은 필수입니다")
  @Email(message = "올바른 이메일 형식이 아닙니다")
  private String email;

  @Pattern(regexp = "^\\d{10,11}$", message = "올바른 전화번호 형식이 아닙니다")
  private String phone;

  @NotBlank(message = "비밀번호는 필수입니다")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
      message = "비밀번호는 8-15자의 대소문자, 숫자, 특수문자를 포함해야 합니다")
  private String password;

  @NotNull(message = "권한은 필수입니다")
  private UserRole role;
}