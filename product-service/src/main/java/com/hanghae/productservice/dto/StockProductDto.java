package com.hanghae.productservice.dto;

import com.hanghae.productservice.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockProductDto {
    private  Long productId;
    private  Integer stock;

    public StockProductDto(Long productId,Integer stock) {
        this.productId = productId;
        this.stock = stock;
    }

}
