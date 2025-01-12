package com.hanghae.orderservice.dto;

import com.hanghae.orderservice.domain.entity.OrderProducts;
import lombok.Getter;

import java.math.BigDecimal;

@Getter

public class OrderProductDto {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    /**
     * 주문 내역 리스트에서 보여지는 상품 정보
     */

    public OrderProductDto(OrderProducts orderProduct, String productName) {
        this.productId = orderProduct.getProductId();
        this.productName = productName;
        this.quantity = orderProduct.getQuantity();
        this.unitPrice = orderProduct.getPrice();
    }

}
