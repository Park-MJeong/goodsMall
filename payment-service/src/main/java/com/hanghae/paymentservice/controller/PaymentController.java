package com.hanghae.paymentservice.controller;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.paymentservice.service.PaymentService;
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
     * 결제 진행화면 진입
     * POST /api/payment/processPayment
     * @param orderId 주문아이디
     * @return 결제성공
     */
    @PostMapping("/processPayment")
    public ResponseEntity<ApiResponse<?>> processPayment(@RequestParam Long orderId, @RequestHeader("X-Claim-userId") Long userId){
        ApiResponse<?> response =null;
        try {
//            카프카 메세지로 재고확인&결제테이블 생성 후 진입 가능
            if(paymentService.isPaymentValid(orderId)){
                response =paymentService.processPayment(orderId);
            }
        }catch (Exception e){
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        return ResponseEntity.ok(response);
    }
}
