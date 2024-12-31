package com.goodsmall.modules.order.controller;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.security.jwt.CustomUserDetails;
import com.goodsmall.modules.order.dto.OrderListRequestDto;
import com.goodsmall.modules.order.dto.OrderRequestDto;
import com.goodsmall.modules.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getOrderList(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestParam(defaultValue = "0") int pageNumber,@RequestParam(defaultValue = "10")int pageSize){

        ApiResponse<?> response =orderService.getOrderList(userDetails.getId(), pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }

    /**
     * 해당 주문 내역 안의 상품 리스트 조회
     * GET /api/orders/{orderId}
     * @param orderId 주문 Id
     * @return 해당 주문 내역 안 상품 리스트
     */

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> getOrderProductList(@PathVariable Long orderId){
        ApiResponse<?> response =orderService.getOrderProductList(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 단건 구매하기 (상품페이지 구매)
     * POST /api/orders/
     * @param dto 제품 Id,수량을 포함하고 있는 요청 객체
     * @return 구매 상품 정보
     */

    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> createOrder(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody OrderRequestDto dto){
        ApiResponse<?> response = orderService.createOrder(userDetails.getId(),dto);
        return ResponseEntity.ok(response);
    }
    /**
     * 상품 다건 구매하기 (장바구니 상품 구매)
     * POST /api/orders/{userId}
     * @param dto 제품 Id,수량을 포함하고 있는 요창 객체 (리스트)
     * @return 해당 주문 내역 안 상품 리스트
     */

    @PostMapping("/carts")
    public ResponseEntity<ApiResponse<?>> createCartOrder(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody OrderListRequestDto dto){
        ApiResponse<?> response = orderService.createCartOrder(userDetails.getId(), dto);
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
}
