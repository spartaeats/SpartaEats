package com.sparta.sparta_eats.user.infrastructure.repository;

import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByUserId(String userId);

  Optional<User> findByEmail(String email);

  boolean existsByUserId(String userId);

  boolean existsByEmail(String email);

  // 삭제되지 않은 사용자만 조회
  Optional<User> findByUserIdAndDeletedAtIsNull(String userId);
}