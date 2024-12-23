package com.goodsmall.modules.order.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateOrderRequestDto {
    private Long userId;
    private Long productId;
    private int quantity;

}
