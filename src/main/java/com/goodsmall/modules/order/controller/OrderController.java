package com.goodsmall.modules.order.controller;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.modules.order.service.OrderService;
import com.goodsmall.modules.product.dto.SliceProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getOrderList(@RequestParam Long userId,@RequestParam(defaultValue = "0") int pageNumber,@RequestParam(defaultValue = "10")int pageSize){

        ApiResponse<?> response =orderService.getOrderList(userId, pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> getOrderProductList(@PathVariable Long orderId,
                                                              @RequestParam(defaultValue = "0") int pageNumber,@RequestParam(defaultValue = "10")int pageSize){
        ApiResponse<?> response =orderService.getOrderProductList(orderId, pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }
}
