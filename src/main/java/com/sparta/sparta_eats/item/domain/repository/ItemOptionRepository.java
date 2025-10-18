package com.sparta.sparta_eats.item.domain.repository;

import com.sparta.sparta_eats.item.domain.entity.ItemOption;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemOptionRepository extends JpaRepository<ItemOption, UUID> {

	//특정 상품의 옵션 목록 조회 (페이징 처리)
	Page<ItemOption> findByItemId(UUID itemId, Pageable pageable);

	// List<ItemOption> findByItemIdOrderByCreatedAtAsc(String itemId); // 상품별 옵션 정렬 조회
	// Page<ItemOption> findByOptionType(Integer optionType, Pageable pageable); // 옵션 타입별 조회
}
