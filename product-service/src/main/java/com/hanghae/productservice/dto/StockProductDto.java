package com.hanghae.productservice.dto;

import com.hanghae.productservice.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockProductDto {
    private long productId;
    private long stock;

    public StockProductDto(Product product) {
        this.productId = product.getId();
        this.stock = product.getQuantity();
    }
}
