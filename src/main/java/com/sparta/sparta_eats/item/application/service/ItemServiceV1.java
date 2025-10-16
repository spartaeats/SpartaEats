package com.sparta.sparta_eats.item.application.service;

import com.sparta.sparta_eats.item.domain.entity.Item;
import com.sparta.sparta_eats.item.domain.repository.ItemCategoryRepository;
import com.sparta.sparta_eats.item.domain.repository.ItemRepository;
import com.sparta.sparta_eats.item.presentation.dto.request.ReqItemCreateDtoV1;
import com.sparta.sparta_eats.item.presentation.dto.request.ReqItemUpdateDtoV1;
import com.sparta.sparta_eats.item.presentation.dto.response.ResItemDtoV1;
import com.sparta.sparta_eats.store.domain.entity.ItemCategory;
import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.store.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceV1 {

    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    @Transactional
    public ResItemDtoV1 createItem(String storeId, ReqItemCreateDtoV1 request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));


        ItemCategory category = itemCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Item item = Item.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .image(request.getImage())
                .active(request.getActive() != null ? request.getActive() : true)
                .soldOut(request.getSoldOut() != null ? request.getSoldOut() : false)
                .createdAt(LocalDateTime.now())
                .createdBy(getCurrentUser())
                .store(store)
                .itemCategory(category)
                .build();

        Item savedItem = itemRepository.save(item);
        return ResItemDtoV1.from(savedItem);
    }

    public Page<ResItemDtoV1> getItemsByStore(String storeId, Pageable pageable) {
        if (!storeRepository.existsById(storeId)) {
            throw new IllegalArgumentException("Store not found");
        }
        return itemRepository.findByStoreId(storeId, pageable)
                .map(ResItemDtoV1::from);
    }

    public Page<ResItemDtoV1> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(ResItemDtoV1::from);
    }

    public ResItemDtoV1 getItemById(String itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        return ResItemDtoV1.from(item);
    }

    @Transactional
    public ResItemDtoV1 updateItem(String itemId, ReqItemUpdateDtoV1 request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }
        if (request.getSalePrice() != null) {
            item.setSalePrice(request.getSalePrice());
        }
        if (request.getImage() != null) {
            item.setImage(request.getImage());
        }
        if (request.getActive() != null) {
            item.setActive(request.getActive());
        }
        if (request.getSoldOut() != null) {
            item.setSoldOut(request.getSoldOut());
        }
        if (request.getCategoryId() != null) {
            ItemCategory category = itemCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            item.setItemCategory(category);
        }

        item.setUpdatedAt(LocalDateTime.now());
        item.setUpdatedBy(getCurrentUser());

        return ResItemDtoV1.from(item);
    }

    @Transactional
    public void deleteItem(String itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.setActive(false);
        item.setUpdatedAt(LocalDateTime.now());
        item.setUpdatedBy(getCurrentUser());
    }

    private String getCurrentUser() {
        // Security Context에서 현재 사용자 정보를 가져오는 로직
        // 예시: SecurityContextHolder.getContext().getAuthentication().getName()
        return "system"; // 임시값
    }
}