package com.goodsmall.modules.order.dto;

import com.goodsmall.modules.order.domain.entity.OrderProducts;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderProductDto {
    private Long productId;
    private String productName;
    private BigDecimal totalProductPrice;
    private int quantity;
    private BigDecimal unitPrice;

    /**
     * 주문 내역 리스트에서 보여지는 상품 정보
     */
    public OrderProductDto(OrderProducts orderProduct) {
        this.productId = orderProduct.getProduct().getId();
        this.productName = orderProduct.getProduct().getProductName();
        this.quantity = Integer.parseInt(orderProduct.getQuantity());
        this.unitPrice = orderProduct.getPrice();

        this.totalProductPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

}
