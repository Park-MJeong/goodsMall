package com.hanghae.orderservice.dto.Order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderListRequestDto {
    private List<Long> cartProductList;
    private long userId;

}