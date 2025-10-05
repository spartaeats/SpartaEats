package com.sparta.sparta_eats.global.infrastructure.config.security;

import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    // ===== 삭제되지 않은 사용자만 조회 =====
    User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

    return new UserDetailsImpl(user);
  }
}