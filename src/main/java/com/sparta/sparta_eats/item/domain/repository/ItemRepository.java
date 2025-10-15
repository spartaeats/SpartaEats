package com.sparta.sparta_eats.item.domain.repository;

import com.sparta.sparta_eats.item.domain.entity.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

	Page<Item> findByStoreId(String storeId, Pageable pageable);

}