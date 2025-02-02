package com.hanghae.productservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductStatus;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ProductDto implements Serializable {
    private Long id;
    private String productName;
    private String description;
    private BigDecimal productPrice;
    private Integer quantity;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime openDate;
    private ProductStatus status;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.productName = product.getProductName();
        this.description = product.getDescription();
        this.productPrice = product.getProductPrice();
        this.quantity = product.getQuantity();
        this.openDate = product.getOpenDate();
        this.status = product.getStatus();
    }

}
