package com.sparta.sparta_eats.item.presentation.controller;

import com.sparta.sparta_eats.item.application.service.ItemOptionServiceV1;
import com.sparta.sparta_eats.item.presentation.dto.request.ReqItemOptionCreateDtoV1;
import com.sparta.sparta_eats.item.presentation.dto.request.ReqItemOptionUpdateDtoV1;
import com.sparta.sparta_eats.item.presentation.dto.response.ResItemOptionDtoV1;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ItemOptionControllerV1 {

	private final ItemOptionServiceV1 itemOptionService;

	//상품별 옵션 등록
	@PostMapping("/items/{itemId}/options")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')") // MANAGER, MASTER, OWNER만 접근 가능
	public ResponseEntity<ResItemOptionDtoV1> createItemOption(
		@PathVariable String itemId,
		@RequestBody ReqItemOptionCreateDtoV1 request) {

		ResItemOptionDtoV1 response = itemOptionService.createItemOption(itemId, request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	//상품별 옵션 조회
	@GetMapping("/items/{itemId}/options")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'MASTER', 'OWNER')") // 모든 역할 접근 가능
	public ResponseEntity<Page<ResItemOptionDtoV1>> getOptionsByItem(
		@PathVariable String itemId,
		Pageable pageable) {

		Page<ResItemOptionDtoV1> response = itemOptionService.getOptionsByItem(itemId, pageable);
		return ResponseEntity.ok(response);
	}

	//옵션 단건 조회
	@GetMapping("/options/{optionId}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'MASTER', 'OWNER')") // 모든 역할 접근 가능
	public ResponseEntity<ResItemOptionDtoV1> getOptionById(@PathVariable String optionId) {

		ResItemOptionDtoV1 response = itemOptionService.getOptionById(optionId);
		return ResponseEntity.ok(response);
	}

	//옵션 수정
	@PatchMapping("/options/{optionId}")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')") // MANAGER, MASTER, OWNER만 접근 가능
	public ResponseEntity<ResItemOptionDtoV1> updateOption(
		@PathVariable String optionId,
		@RequestBody ReqItemOptionUpdateDtoV1 request) {

		ResItemOptionDtoV1 response = itemOptionService.updateOption(optionId, request);
		return ResponseEntity.ok(response);
	}

	//옵션 삭제 (Soft Delete)
	@DeleteMapping("/options/{optionId}")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')") // MANAGER, MASTER, OWNER만 접근 가능
	public ResponseEntity<Void> deleteOption(@PathVariable String optionId) {

		itemOptionService.deleteOption(optionId);

		return ResponseEntity.noContent().build();
	}
}