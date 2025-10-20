package com.sparta.sparta_eats.cart.application.service;

import com.sparta.sparta_eats.cart.application.dto.CartSnapshot;
import com.sparta.sparta_eats.cart.application.dto.CreateCartCommand;
import com.sparta.sparta_eats.cart.application.dto.CreateCartResult;

import java.util.List;
import java.util.UUID;

public interface CartService {

    CreateCartResult createOrGetCart(
            String userId,
            UUID storeId,
            boolean forceReplace,
            List<CreateCartCommand.Item> items,
            UUID addressId
    );

    CartSnapshot getCart(String userId, UUID cartId);
    
    // 사용자 ID로 장바구니 조회 (없으면 null)
    CartSnapshot getCartByUserId(String userId);

    // 장바구니 삭제
    void deleteCart(String userId, UUID cartId);
    
    // 장바구니 아이템 수량 변경
    CartSnapshot changeCartItemQuantity(String userId, UUID cartItemId, int quantity);
}



