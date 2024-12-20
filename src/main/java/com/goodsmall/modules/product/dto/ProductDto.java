package com.goodsmall.modules.product.dto;

import java.time.LocalDateTime;

public class ProductDto {
    private String productName;
    private int price;
    private String image;
    private LocalDateTime openDate;

    public ProductDto(ProductDto productDto) {
        this.productName = productDto.productName;
        this.price = productDto.price;
        this.image = productDto.image;
        this.openDate = productDto.openDate;

    }
}
