package com.sparta.sparta_eats.global.infrastructure.config.security;

import com.sparta.sparta_eats.user.domain.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

  // ===== JWT 관련 상수 =====
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String AUTHORIZATION_KEY = "auth";
  public static final String BEARER_PREFIX = "Bearer ";

  // ===== JWT Secret Key & 만료시간 (application.yml에서 주입) =====
  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.expiration-time}")
  private long expirationTime;

  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  // ===== Secret Key 초기화 =====
  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  // ===== JWT 토큰 생성 =====
  public String createToken(String userId, UserRole role) {
    Date date = new Date();

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject(userId)  // 사용자 ID
            .claim(AUTHORIZATION_KEY, role.getAuthority())  // 사용자 권한
            .setExpiration(new Date(date.getTime() + expirationTime))  // 만료 시간
            .setIssuedAt(date)  // 발급일
            .signWith(key, signatureAlgorithm)  // 암호화 알고리즘
            .compact();
  }

  // ===== HTTP Header에서 JWT 가져오기 =====
  public String getJwtFromHeader(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(7);  // "Bearer " 제거
    }
    return null;
  }

  // ===== JWT 검증 =====
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT token, 만료된 JWT token 입니다.");
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
    } catch (IllegalArgumentException e) {
      log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
    }
    return false;
  }

  // ===== JWT에서 사용자 정보 가져오기 =====
  public Claims getUserInfoFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}