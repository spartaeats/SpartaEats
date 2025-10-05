package com.sparta.sparta_eats.user.presentation.controller;

import com.sparta.sparta_eats.user.application.service.UserService;
import com.sparta.sparta_eats.user.presentation.dto.request.LoginRequest;
import com.sparta.sparta_eats.user.presentation.dto.request.SignupRequest;
import com.sparta.sparta_eats.user.presentation.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "회원가입 및 로그인 관련 API")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  // ===== 회원가입 =====
  @Operation(summary = "회원가입", description = "새로운 사용자 계정을 생성하고 JWT 토큰을 발급합니다.")
  @PostMapping("/signup")
  public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
    AuthResponse response = userService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // ===== 로그인 (참고용: 실제로는 JwtAuthenticationFilter에서 처리) =====
  // Filter를 사용하지 않고 직접 로그인 처리하고 싶다면 이 엔드포인트 사용
  @Operation(summary = "로그인", description = "기존 계정으로 로그인하고 JWT 토큰을 발급합니다.")
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = userService.login(request);
    return ResponseEntity.ok(response);
  }
}