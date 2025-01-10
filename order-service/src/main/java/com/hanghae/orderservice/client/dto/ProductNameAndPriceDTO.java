package com.hanghae.orderservice.client.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductNameAndPriceDTO {
    private Long productId;
    private BigDecimal productPrice;
    private String productName;
}
