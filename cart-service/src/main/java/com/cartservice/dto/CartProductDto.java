package com.cartservice.dto;

import com.cartservice.domain.entity.CartProducts;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CartProductDto {
    private Long productId;
    private String productName;
    private BigDecimal totalProductPrice;
    private int quantity;

//    public CartProductDto(CartProducts cartProduct) {
//        this.productId = cartProduct.getProductId();
//        this.productName = cartProduct.getProduct().getProductName();
//        this.quantity = cartProduct.getQuantity();
//        this.totalProductPrice = cartProduct.getProduct().getProductPrice().multiply(BigDecimal.valueOf(this.quantity));
//    }
}
