package com.sparta.sparta_eats.store.domain.repository;

import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);

    // 삭제되지 않은 사용자만 조회
    Optional<User> findByUserIdAndDeletedAtIsNull(String userId);
}
