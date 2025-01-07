package com.hanghae.orderservice.controller;


import com.esotericsoftware.minlog.Log;
import com.hanghae.common.api.ApiResponse;
import com.hanghae.orderservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

//
//    @PostMapping("/initPayment")
//    public ResponseEntity<ApiResponse<?>> initPayment(@RequestParam Long orderId,@RequestHeader("X-Claim-userId") Long userId){
//        log.info("[결제화면 진입 API] userId{}",userId);
//        ApiResponse<?> response =paymentService.initPayment(orderId,userId);
//        return ResponseEntity.ok(response);
//    }

    /**
     * 결제 진행중
     * POST /api/payment/processPayment
     * @param orderId 주문아이디
     * @return 결제성공
     */
    @PostMapping("/processPayment")
    public ResponseEntity<ApiResponse<?>> processPayment(@RequestParam Long orderId,@RequestHeader("X-Claim-userId") Long userId){
        ApiResponse<?> response =paymentService.createPayment(orderId,userId);
        return ResponseEntity.ok(response);
    }

}
