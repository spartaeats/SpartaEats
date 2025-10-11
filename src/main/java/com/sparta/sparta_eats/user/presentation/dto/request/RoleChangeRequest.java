package com.sparta.sparta_eats.user.presentation.dto.request;

import com.sparta.sparta_eats.user.domain.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoleChangeRequest {

  @NotNull(message = "변경할 권한은 필수입니다")
  private UserRole role;
}