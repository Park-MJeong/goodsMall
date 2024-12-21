package com.goodsmall.modules.product.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductDto {
    private String productName;
    private String description;
    private String image;
    private Long productPrice;
    private LocalDateTime openDate;
    private String status;

    public ProductDto(String productName, String description, String image, Long productPrice, LocalDateTime openDate, String status) {
        this.productName = productName;
        this.description = description;
        this.image = image;
        this.productPrice = productPrice;
        this.openDate = openDate;
        this.status = status;
    }
}
