package com.sparta.sparta_eats.item.application.service;

import com.sparta.sparta_eats.item.domain.entity.Item;
import com.sparta.sparta_eats.item.domain.repository.ItemOptionRepository;
import com.sparta.sparta_eats.item.domain.repository.ItemRepository;
import com.sparta.sparta_eats.item.domain.entity.ItemOption;
import com.sparta.sparta_eats.item.presentation.dto.request.ReqItemOptionCreateDtoV1;
import com.sparta.sparta_eats.item.presentation.dto.request.ReqItemOptionUpdateDtoV1;
import com.sparta.sparta_eats.item.presentation.dto.response.ResItemOptionDtoV1;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemOptionServiceV1 {

	private final ItemOptionRepository itemOptionRepository;
	private final ItemRepository itemRepository;

	//옵션 생성
	@Transactional
	public ResItemOptionDtoV1 createItemOption(String itemId, ReqItemOptionCreateDtoV1 request) {

		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new IllegalArgumentException("Item not found"));

		ItemOption itemOption = ItemOption.builder()
			.id(UUID.randomUUID())
			.name(request.getName())
			.optionType(request.getOptionType())
			.addPrice(request.getAddPrice())
			.createdAt(LocalDateTime.now())
			.createdBy(getCurrentUser())
			.item(item)
			.build();

		ItemOption savedOption = itemOptionRepository.save(itemOption);

		return ResItemOptionDtoV1.from(savedOption);
	}

	//상품별 옵션 목록 조회
	public Page<ResItemOptionDtoV1> getOptionsByItem(String itemId, Pageable pageable) {
		if (!itemRepository.existsById(itemId)) {
			throw new IllegalArgumentException("Item not found");
		}

		return itemOptionRepository.findByItemId(itemId, pageable)
			.map(ResItemOptionDtoV1::from);
	}

	//옵션 단건 조회
	public ResItemOptionDtoV1 getOptionById(String optionId) {

		ItemOption itemOption = itemOptionRepository.findById(optionId)
			.orElseThrow(() -> new IllegalArgumentException("Option not found"));

		return ResItemOptionDtoV1.from(itemOption);
	}

	//옵션 수정
	@Transactional
	public ResItemOptionDtoV1 updateOption(String optionId, ReqItemOptionUpdateDtoV1 request) {

		ItemOption itemOption = itemOptionRepository.findById(optionId)
			.orElseThrow(() -> new IllegalArgumentException("Option not found"));

		if (request.getName() != null) {
			itemOption.setName(request.getName());
		}
		if (request.getOptionType() != null) {
			itemOption.setOptionType(request.getOptionType());
		}
		if (request.getAddPrice() != null) {
			itemOption.setAddPrice(request.getAddPrice());
		}

		itemOption.setUpdatedAt(LocalDateTime.now());
		itemOption.setUpdatedBy(getCurrentUser());

		// JPA의 변경 감지(Dirty Checking)로 자동 저장됨
		return ResItemOptionDtoV1.from(itemOption);
	}

	//옵션 삭제
	@Transactional
	public void deleteOption(String optionId) {
		ItemOption itemOption = itemOptionRepository.findById(optionId)
			.orElseThrow(() -> new IllegalArgumentException("Option not found"));

		// ItemOption은 active 필드가 없으므로 Hard Delete
		itemOptionRepository.delete(itemOption);
	}

	//현재 로그인한 사용자 정보 조회
	private String getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 인증 정보가 없거나 익명 사용자인 경우
		if (authentication == null || !authentication.isAuthenticated()
			|| "anonymousUser".equals(authentication.getPrincipal())) {
			return "system"; // 기본값 반환
		}

		return authentication.getName();
	}
}