package com.sparta.sparta_eats.cart.application.dto;

//서비스 결과 요약
public record CreateCartResult(
        CartSnapshot cart,
        boolean created,
        boolean reused,
        boolean replacedPreviousStoreCart
) {}

