package com.goodsmall.modules.cart.dto;

import com.goodsmall.modules.cart.domain.entity.CartProducts;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CartProductDto {
    private Long productId;
    private String productName;
    private BigDecimal unitProductPrice;
    private BigDecimal totalProductPrice;
    private int quantity;

    public CartProductDto(CartProducts cartProduct) {
        this.productId = cartProduct.getProduct().getId();
        this.productName = cartProduct.getProduct().getProductName();
        this.quantity = cartProduct.getQuantity();
        this.unitProductPrice = cartProduct.getProduct().getProductPrice();
        this.totalProductPrice = cartProduct.getProduct().getProductPrice().multiply(BigDecimal.valueOf(this.quantity));
    }
}
