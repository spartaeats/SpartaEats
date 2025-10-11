package com.sparta.sparta_eats.user.presentation.controller;

import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import com.sparta.sparta_eats.user.application.service.UserService;
import com.sparta.sparta_eats.user.presentation.dto.request.PasswordChangeRequest;
import com.sparta.sparta_eats.user.presentation.dto.request.RoleChangeRequest;
import com.sparta.sparta_eats.user.presentation.dto.request.UserUpdateRequest;
import com.sparta.sparta_eats.user.presentation.dto.response.MessageResponse;
import com.sparta.sparta_eats.user.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

  // ===== 내 정보 수정 =====
  @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
  @PatchMapping("/me")
  public ResponseEntity<UserResponse> updateMyInfo(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      @Valid @RequestBody UserUpdateRequest request) {
    UserResponse response = userService.updateMyInfo(userDetails.getUsername(), request);
    return ResponseEntity.ok(response);
  }

  // ===== 비밀번호 변경 =====
  @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다.")
  @PutMapping("/me/password")
  public ResponseEntity<MessageResponse> changePassword(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      @Valid @RequestBody PasswordChangeRequest request) {
    MessageResponse response = userService.changePassword(userDetails.getUsername(), request);
    return ResponseEntity.ok(response);
  }

  // ===== 회원 탈퇴 =====
  @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 탈퇴(soft delete) 처리합니다.")
  @DeleteMapping("/me")
  public ResponseEntity<MessageResponse> deleteMyAccount(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    MessageResponse response = userService.deleteMyAccount(userDetails.getUsername());
    return ResponseEntity.ok(response);
  }

  // ===== 사용자 목록 조회 (관리자용) =====
  @Operation(summary = "사용자 목록 조회", description = "모든 사용자 목록을 조회합니다. (관리자 전용)")
  @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
  @GetMapping
  public ResponseEntity<Page<UserResponse>> getAllUsers(
      @PageableDefault(size = 10) Pageable pageable) {
    Page<UserResponse> response = userService.getAllUsers(pageable);
    return ResponseEntity.ok(response);
  }

  // ===== 특정 사용자 조회 (관리자용) =====
  @Operation(summary = "특정 사용자 조회", description = "userId로 특정 사용자의 정보를 조회합니다. (관리자 전용)")
  @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
    UserResponse response = userService.getUserById(userId);
    return ResponseEntity.ok(response);
  }

  // ===== 권한 변경 (관리자용) =====
  @Operation(summary = "사용자 권한 변경", description = "특정 사용자의 권한을 변경합니다. (관리자 전용)")
  @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
  @PatchMapping("/{userId}/role")
  public ResponseEntity<UserResponse> changeUserRole(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      @PathVariable String userId,
      @Valid @RequestBody RoleChangeRequest request) {
    UserResponse response = userService.changeUserRole(
        userDetails.getUsername(),
        userId,
        request
    );
    return ResponseEntity.ok(response);
  }

  // ===== 👇 사용자 삭제 (관리자용, 새로 추가) =====
  @Operation(summary = "사용자 삭제", description = "특정 사용자를 삭제(soft delete) 처리합니다. (관리자 전용)")
  @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
  @DeleteMapping("/{userId}")
  public ResponseEntity<MessageResponse> deleteUser(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      @PathVariable String userId) {
    MessageResponse response = userService.deleteUser(
        userDetails.getUsername(),
        userId
    );
    return ResponseEntity.ok(response);
  }
}