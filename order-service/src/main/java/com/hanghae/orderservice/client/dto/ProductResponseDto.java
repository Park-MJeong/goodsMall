package com.hanghae.orderservice.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponseDto {
    private Long id;
    private String productName;
    private BigDecimal productPrice;
    private int quantity;
    private String status;

}
