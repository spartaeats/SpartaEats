package com.sparta.sparta_eats.item.domain.repository;

import com.sparta.sparta_eats.item.domain.entity.Item;

import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

	Page<Item> findByStoreId(UUID storeId, Pageable pageable);
}