package com.hanghae.orderservice.controller;


import com.hanghae.common.api.ApiResponse;
import com.hanghae.orderservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 해당 주문 내역 상세
     * GET /api/orders/{orderId}
     * @param orderId 주문아이디
     * @return 해당 주문 내역 안 상품 리스트
     */

    @PostMapping("/processPayment")
    public ResponseEntity<ApiResponse<?>> processPayment(@RequestParam Long orderId){
        ApiResponse<?> response =paymentService.createPayment(orderId);
        return ResponseEntity.ok(response);

    }

}
