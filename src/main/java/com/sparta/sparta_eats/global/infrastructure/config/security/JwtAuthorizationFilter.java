package com.sparta.sparta_eats.global.infrastructure.config.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;

  // 퍼블릭 엔드포인트는 필터 자체를 스킵
  private static final AntPathMatcher MATCHER = new AntPathMatcher();
  private static final List<String> WHITELIST = List.of(
      "/v1/auth/**", "/auth/**", "/swagger-ui/**", "/v3/api-docs/**"
  );

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return WHITELIST.stream().anyMatch(p -> MATCHER.match(p, uri));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    // Authorization 헤더에서 토큰 추출
    String tokenValue = jwtUtil.getJwtFromHeader(request);

    // 토큰이 없으면 인증 시도 없이 다음 필터로 진행 (permitAll/익명 접근과 조화)
    if (!StringUtils.hasText(tokenValue)) {
      chain.doFilter(request, response);
      return;
    }

    // 토큰이 있으면 검증
    if (!jwtUtil.validateToken(tokenValue)) {
      log.error("Token Error");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    // 인증 컨텍스트 생성/주입
    Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
    setAuthentication(info.getSubject());

    chain.doFilter(request, response);
  }

  private void setAuthentication(String userId) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = createAuthentication(userId);
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
  }

  private Authentication createAuthentication(String userId) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }
}
