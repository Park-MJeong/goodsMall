package com.hanghae.orderservice.controller;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.orderservice.dto.OrderListRequestDto;
import com.hanghae.orderservice.dto.OrderProductStock;
import com.hanghae.orderservice.dto.OrderRequestDto;
import com.hanghae.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 주문내역 전체 리스트 조회
     * GET /api/orders/
     * @param pageNumber 페이지번호
     * @param pageSize  페이지당 수량
     * @return 주문내역 리스트
     */

    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getOrderList(@RequestHeader("X-Claim-userId") long userId,
                                                       @RequestParam(defaultValue = "0") int pageNumber,
                                                       @RequestParam(defaultValue = "10")int pageSize){

        ApiResponse<?> response =orderService.getOrderList(userId, pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }

    /**
     * 해당 주문 내역 상세
     * GET /api/orders/{orderId}
     * @param orderId 주문 Id
     * @return 해당 주문 내역 안 상품 리스트
     */

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> getOrderProductList(@PathVariable Long orderId){
        ApiResponse<?> response =orderService.getOrderProduct(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 단건 구매하기 (상품페이지 구매)
     * POST /api/orders/
     * @param requestDto 유저 id, 제품 id,수량을 포함하고 있는 요청 객체
     * @return 구매 상품 정보
     */

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createOrder(@RequestHeader("X-Claim-userId") long userId, @RequestBody OrderRequestDto requestDto){
        ApiResponse<?> response = orderService.createOrder(userId,requestDto);
        return ResponseEntity.ok(response);
    }
    /**
     * 상품 다건 구매하기 (장바구니 상품 구매)
     * POST /api/orders/{userId}
     * @param requestDto 제품 Id,수량을 포함하고 있는 요창 객체 (리스트)
     * @return 해당 주문 내역 안 상품 리스트
     */
    @PostMapping("/carts")
    public ResponseEntity<ApiResponse<?>> createCartOrder(@RequestBody OrderListRequestDto requestDto){
        ApiResponse<?> response = orderService.createCartOrder(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 상태 수정하기(주문취소,환불)
     * POST /api/orders/{userId}
     * @param orderId 주문Id
     * @return 수정된 주문 정보
     */

    @PostMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId){
        ApiResponse<?> response = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * 해당 주문 상품들의 재고
     */
    @GetMapping("/orderProductStock/{orderId}")
    public List<OrderProductStock> orderProductStock(@PathVariable Long orderId){
        return orderService.getOrderProductStockList(orderId);
    }
}
