package com.sparta.sparta_eats.store.domain.repository;

import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, String> {
}
