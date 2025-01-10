package com.hanghae.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderListRequestDto {
    private List<Long> productIdList;
    private long userId;

}