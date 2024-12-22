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


    /*등록되어 있는 상품 리스트 조회*/
//    @GetMapping("/")
//    public ResponseEntity<Slice<SliceProductDto>> getProducts(@RequestParam(value = "search")String search,
//                                                              @RequestParam(value = "cursor",required = false)Integer cursor,
//                                                              @RequestParam(value = "size",required = false)Integer size){
//        if (cursor == null) {
//            cursor = 0;
//        }
////        Slice<SliceProductDto> result = productService.getProductList(search, cursor, size);
//        return ResponseEntity.ok(result);
//    }
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getOrderList(@PathVariable Long userId,@RequestParam Long cursor,@RequestParam(defaultValue = "10")int size){
        if (cursor == null) {
            cursor = 0L;
        }
        ApiResponse<?> response =orderService.getOrderList(userId, cursor, size);
        return ResponseEntity.ok(response);
    }
}
