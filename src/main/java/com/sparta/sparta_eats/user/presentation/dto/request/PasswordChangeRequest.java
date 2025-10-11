package com.sparta.sparta_eats.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordChangeRequest {

  @NotBlank(message = "현재 비밀번호는 필수입니다")
  private String currentPassword;

  @NotBlank(message = "새 비밀번호는 필수입니다")
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
      message = "새 비밀번호는 8-15자의 대소문자, 숫자, 특수문자를 포함해야 합니다")
  private String newPassword;

  @NotBlank(message = "새 비밀번호 확인은 필수입니다")
  private String newPasswordConfirm;
}