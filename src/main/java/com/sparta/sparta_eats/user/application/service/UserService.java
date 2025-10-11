package com.sparta.sparta_eats.user.application.service;

import com.sparta.sparta_eats.global.domain.exception.BadRequestException;
import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.global.infrastructure.config.security.JwtUtil;
import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.infrastructure.repository.UserRepository;
import com.sparta.sparta_eats.user.presentation.dto.request.LoginRequest;
import com.sparta.sparta_eats.user.presentation.dto.request.SignupRequest;
import com.sparta.sparta_eats.user.presentation.dto.request.UserUpdateRequest;
import com.sparta.sparta_eats.user.presentation.dto.response.AuthResponse;
import com.sparta.sparta_eats.user.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  // ===== 회원가입 =====
  @Transactional
  public AuthResponse signup(SignupRequest request) {
    // 1. userId 중복 확인
    if (userRepository.existsByUserId(request.getUserId())) {
      throw new BadRequestException("이미 존재하는 사용자 ID입니다.");
    }

    // 2. email 중복 확인
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("이미 존재하는 이메일입니다.");
    }

    // 3. 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(request.getPassword());

    // 4. User 엔티티 생성 및 저장
    User user = User.builder()
        .userId(request.getUserId())
        .nickname(request.getNickname())
        .email(request.getEmail())
        .phone(request.getPhone())
        .password(encodedPassword)
        .role(request.getRole())
        .isPublic(false)  // 기본값: 비공개
        .build();

    userRepository.save(user);

    // 5. JWT 토큰 생성
    String token = jwtUtil.createToken(user.getUserId(), user.getRole());

    // 6. 응답 생성
    return AuthResponse.of(
        "회원가입이 완료되었습니다.",
        token,
        UserResponse.from(user)
    );
  }

  // ===== 로그인 (참고: 실제 로그인은 JwtAuthenticationFilter에서 처리) =====
  // 이 메서드는 Filter를 통하지 않고 직접 로그인할 때 사용
  @Transactional
  public AuthResponse login(LoginRequest request) {
    // 1. 사용자 조회
    User user = userRepository.findByUserIdAndDeletedAtIsNull(request.getUserId())
        .orElseThrow(() -> new BadRequestException("사용자를 찾을 수 없습니다."));

    // 2. 비밀번호 확인
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadRequestException("비밀번호가 일치하지 않습니다.");
    }

    // 3. 마지막 로그인 시간 업데이트
    user.updateLastLogin();

    // 4. JWT 토큰 생성
    String token = jwtUtil.createToken(user.getUserId(), user.getRole());

    // 5. 응답 생성
    return AuthResponse.of(
        "로그인이 완료되었습니다.",
        token,
        UserResponse.from(user)
    );
  }

  // ===== 내 정보 조회 =====
  public UserResponse getMyInfo(String userId) {
    // 1. 사용자 조회 (삭제되지 않은 사용자만)
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

    // 2. 응답 생성
    return UserResponse.from(user);
  }

  // ===== 👇 사용자 정보 수정 (새로 추가) =====
  @Transactional
  public UserResponse updateMyInfo(String userId, UserUpdateRequest request) {
    // 1. 사용자 조회
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

    // 2. 이메일 중복 체크 (변경하려는 경우만)
    if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
      if (userRepository.existsByEmail(request.getEmail())) {
        throw new BadRequestException("이미 존재하는 이메일입니다.");
      }
    }

    // 3. 정보 업데이트
    user.updateInfo(
        request.getNickname(),
        request.getEmail(),
        request.getPhone(),
        request.getIsPublic()
    );

    // 4. 응답 생성
    return UserResponse.from(user);
  }
}