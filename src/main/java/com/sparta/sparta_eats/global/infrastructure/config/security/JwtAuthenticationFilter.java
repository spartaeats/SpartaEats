package com.sparta.sparta_eats.global.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sparta_eats.user.presentation.dto.request.LoginRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
    setFilterProcessesUrl("/v1/auth/login");  // 로그인 URL 설정
  }

  // ===== 로그인 시도 =====
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    log.info("로그인 시도");
    try {
      LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

      return getAuthenticationManager().authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getUserId(),
              loginRequest.getPassword(),
              null
          )
      );
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  // ===== 로그인 성공 시 JWT 생성 =====
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult)
      throws IOException, ServletException {
    log.info("로그인 성공 및 JWT 생성");

    String userId = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
    String role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole().name();

    // JWT 생성
    String token = jwtUtil.createToken(
        userId,
        ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole()
    );

    // Response Header에 JWT 추가
    response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
    response.setStatus(HttpServletResponse.SC_OK);
  }

  // ===== 로그인 실패 =====
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed)
      throws IOException, ServletException {
    log.info("로그인 실패: {}", failed.getMessage());
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}