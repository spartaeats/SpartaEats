package com.sparta.sparta_eats.user.presentation.controller;

import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import com.sparta.sparta_eats.user.application.service.UserService;
import com.sparta.sparta_eats.user.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 API", description = "사용자 정보 관리 관련 API")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // ===== 내 정보 조회 =====
  @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMyInfo(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    UserResponse response = userService.getMyInfo(userDetails.getUsername());
    return ResponseEntity.ok(response);
  }
}