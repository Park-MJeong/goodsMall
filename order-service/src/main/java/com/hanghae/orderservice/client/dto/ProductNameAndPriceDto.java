package com.hanghae.orderservice.client.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductNameAndPriceDto {
    private Long productId;
    private BigDecimal productPrice;
    private String productName;
}
