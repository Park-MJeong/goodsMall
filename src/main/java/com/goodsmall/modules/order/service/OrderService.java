package com.goodsmall.modules.order.service;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.util.SliceUtil;
import com.goodsmall.modules.order.domain.OrderProductRepository;
import com.goodsmall.modules.order.domain.OrderRepository;
import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.dto.OrderListDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderService {
    private final OrderRepository oRepository;
    private final OrderProductRepository opRepository;

    public OrderService(OrderRepository oRepository, OrderProductRepository opRepository) {
        this.oRepository = oRepository;
        this.opRepository = opRepository;
    }


    public ApiResponse<?> getOrderList(Long userId,Long cursor,Integer size){
        Slice<Order> showOrderList = oRepository.findOrdersWithProducts(userId,cursor, Pageable.ofSize(size+1));

        List<OrderListDto> listDto = showOrderList.getContent().stream()
                .map(OrderListDto::new)
                .collect(Collectors.toList());

        return ApiResponse.success(SliceUtil.getSlice(listDto,size));
    }




}
