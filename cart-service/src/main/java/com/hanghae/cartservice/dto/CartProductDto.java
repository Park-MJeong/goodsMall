package com.hanghae.cartservice.dto;

import com.hanghae.cartservice.client.dto.ProductResponseDto;
import com.hanghae.cartservice.domain.entity.CartProducts;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CartProductDto {
    private Long productId;
    private String productName;
    private BigDecimal unitProductPrice;
    private BigDecimal totalProductPrice;
    private int quantity;

    public CartProductDto(CartProducts cartProduct, ProductResponseDto product) {
        this.productId = cartProduct.getProductId();
        this.productName = product.getProductName();
        this.quantity = cartProduct.getQuantity();
        this.unitProductPrice = product.getProductPrice();
        this.totalProductPrice = product.getProductPrice().multiply(BigDecimal.valueOf(this.quantity));
    }
}
