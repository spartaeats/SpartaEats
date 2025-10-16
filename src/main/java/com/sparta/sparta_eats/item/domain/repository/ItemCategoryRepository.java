package com.sparta.sparta_eats.item.domain.repository;

import com.sparta.sparta_eats.store.domain.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemCategoryRepository extends JpaRepository<ItemCategory, String> {
}
