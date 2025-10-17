package com.sparta.sparta_eats.cart.application.dto;

import java.util.List;
import java.util.UUID;


//서비스 입력 전용 DTO
public class CreateCartCommand {

    public record Item(
            UUID itemId,
            int quantity,
            List<Option> options
    ) {}

    public record Option(
            UUID itemOptionId,
            int quantity
    ) {}
}

