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
            List<CreateCartCommand.Item> items
    );

    CartSnapshot getCart(String userId, UUID cartId);

    // 장바구니 삭제
    void deleteCart(String userId, UUID cartId);
}



