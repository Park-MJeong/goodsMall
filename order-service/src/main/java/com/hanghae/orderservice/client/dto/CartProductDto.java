package com.hanghae.orderservice.client.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CartProductDto {
    private Long productId;
    private String productName;
    private BigDecimal unitProductPrice;
    private BigDecimal totalProductPrice;
    private int quantity;
}
