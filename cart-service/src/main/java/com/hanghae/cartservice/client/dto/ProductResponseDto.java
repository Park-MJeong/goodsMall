package com.hanghae.cartservice.client.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductResponseDto {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal productPrice;
    private String status;
}
