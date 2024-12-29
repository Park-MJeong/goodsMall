package com.orderservice.dto;


import lombok.Getter;

@Getter
public class OrderRequestDto {
    private Long productId;
    private int quantity;

}
