package com.hanghae.orderservice.dto.Order;

import com.hanghae.orderservice.client.dto.ProductResponseDto;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderProductDto {
    private Long productId;
    private String productName;
//    private BigDecimal totalProductPrice;
    private int quantity;
    private BigDecimal unitPrice;

    /**
     * 주문 내역 리스트에서 보여지는 상품 정보
     */

    public OrderProductDto(OrderProducts orderProduct, ProductResponseDto productResponseDto) {
        this.productId = orderProduct.getProductId();
        this.productName = productResponseDto.getProductName();
        this.quantity = orderProduct.getQuantity();
        this.unitPrice = orderProduct.getPrice();
//        this.totalProductPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

}
