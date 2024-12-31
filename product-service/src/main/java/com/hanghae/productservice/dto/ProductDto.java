package com.hanghae.productservice.dto;

import com.hanghae.productservice.domain.Product;
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

    public ProductDto(Product product) {
        this.productName = product.getProductName();
        this.description = product.getDescription();
        this.productPrice = product.getProductPrice();
        this.openDate = product.getOpenDate();
        this.status = product.getStatus();
    }

}
