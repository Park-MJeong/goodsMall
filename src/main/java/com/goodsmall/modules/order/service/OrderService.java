package com.goodsmall.modules.order.service;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.util.SliceUtil;
import com.goodsmall.modules.order.domain.OrderProductRepository;
import com.goodsmall.modules.order.domain.OrderRepository;
import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import com.goodsmall.modules.order.dto.OrderListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository oRepository;
    private final OrderProductRepository opRepository;

    public OrderService(OrderRepository oRepository, OrderProductRepository opRepository) {
        this.oRepository = oRepository;
        this.opRepository = opRepository;
    }


    public ApiResponse<?> getOrderList(Long userId,int pageNumber,int pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orderList = oRepository.getOrderList(userId,pageable);

        List<OrderListDto> listDto = orderList.getContent().stream()
                .map(OrderListDto::new)
                .collect(Collectors.toList());

        return ApiResponse.success(listDto);
    }

    public ApiResponse<?> getOrderProductList(Long orderId){
        Order orderProductList = oRepository.getOrderProductsList(orderId);
        OrderListDto listDto = new OrderListDto(orderProductList);


        return ApiResponse.success(listDto);
    }




}
