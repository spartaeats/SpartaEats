package com.sparta.sparta_eats.cart.application.exception;

import java.util.UUID;

/**
 * 다른 매장의 장바구니를 만들려고 할 때 발생하는 예외.
 * 예) 이미 A매장 장바구니가 있는데 B매장 상품을 담으려는 경우.
 */
public class StoreMismatchException extends RuntimeException {

    public StoreMismatchException(UUID existingStoreId, UUID newStoreId) {
        super(String.format("기존 장바구니는 매장 %s 의 상품입니다. 새로운 매장 %s 으로 교체하려면 forceReplace=true 로 요청하세요.",
                existingStoreId, newStoreId));
    }
}

