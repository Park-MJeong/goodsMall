package com.goodsmall.modules.order.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderProductListDto {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private int totalQuantity;
    private String status;
    private List<OrderProductDto> products;

}
