package com.sparta.sparta_eats.item.domain.repository;

import com.sparta.sparta_eats.store.domain.entity.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemOptionRepository extends JpaRepository<ItemOption, UUID> {
}
