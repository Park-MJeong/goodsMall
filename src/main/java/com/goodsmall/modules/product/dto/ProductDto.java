package com.goodsmall.modules.product.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ProductDto {
    private String productName;
    private String description;
    private BigDecimal productPrice;
    private LocalDateTime openDate;
    private String status;

    public ProductDto(String productName, String description, BigDecimal productPrice, LocalDateTime openDate, String status) {
        this.productName = productName;
        this.description = description;
        this.productPrice = productPrice;
        this.openDate = openDate;
        this.status = status;
    }
}
