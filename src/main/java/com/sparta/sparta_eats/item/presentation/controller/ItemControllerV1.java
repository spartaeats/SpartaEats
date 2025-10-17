package com.sparta.sparta_eats.item.presentation.controller;

import com.sparta.sparta_eats.item.application.service.ItemServiceV1;
import com.sparta.sparta_eats.item.presentation.dto.request.ReqItemCreateDtoV1;
import com.sparta.sparta_eats.item.presentation.dto.request.ReqItemUpdateDtoV1;
import com.sparta.sparta_eats.item.presentation.dto.response.ResItemDtoV1;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ItemControllerV1 {

	private final ItemServiceV1 itemService;

	//매장별 상품 등록
	@PostMapping("/stores/{storeId}/items")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
	public ResponseEntity<ResItemDtoV1> createItem(
		@PathVariable UUID storeId,
		@RequestBody ReqItemCreateDtoV1 request) {
		ResItemDtoV1 response = itemService.createItem(storeId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	//매장별 상품 조회
	@GetMapping("/stores/{storeId}/items")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'MASTER', 'OWNER')")
	public ResponseEntity<Page<ResItemDtoV1>> getItemsByStore(
		@PathVariable UUID storeId,
		Pageable pageable) {
		Page<ResItemDtoV1> response = itemService.getItemsByStore(storeId, pageable);
		return ResponseEntity.ok(response);
	}

	//상품 전체 조회
	@GetMapping("/items")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'MASTER', 'OWNER')")
	public ResponseEntity<Page<ResItemDtoV1>> getAllItems(Pageable pageable) {
		Page<ResItemDtoV1> response = itemService.getAllItems(pageable);
		return ResponseEntity.ok(response);
	}

	//상품 단건 조회
	@GetMapping("/items/{itemId}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER', 'MASTER', 'OWNER')")
	public ResponseEntity<ResItemDtoV1> getItemById(@PathVariable UUID itemId) {
		ResItemDtoV1 response = itemService.getItemById(itemId);
		return ResponseEntity.ok(response);
	}

	//상품 수정
	@PatchMapping("/items/{itemId}")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
	public ResponseEntity<ResItemDtoV1> updateItem(
		@PathVariable UUID itemId,
		@RequestBody ReqItemUpdateDtoV1 request) {
		ResItemDtoV1 response = itemService.updateItem(itemId, request);
		return ResponseEntity.ok(response);
	}

	//상품 삭제 Soft Delete
	@DeleteMapping("/items/{itemId}")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER', 'OWNER')")
	public ResponseEntity<Void> deleteItem(@PathVariable UUID itemId) {
		itemService.deleteItem(itemId);
		return ResponseEntity.noContent().build();
	}
}