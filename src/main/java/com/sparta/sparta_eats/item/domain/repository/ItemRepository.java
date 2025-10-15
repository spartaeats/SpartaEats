package com.sparta.sparta_eats.item.domain.repository;

import com.sparta.sparta_eats.item.domain.entity.Item;

import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

	Page<Item> findByStoreId(String storeId, Pageable pageable);

    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);

    // 삭제되지 않은 사용자만 조회
    Optional<User> findByUserIdAndDeletedAtIsNull(String userId);
}