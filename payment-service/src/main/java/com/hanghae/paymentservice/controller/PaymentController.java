package com.hanghae.paymentservice.controller;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    @PostMapping("/process-payment")
    public ResponseEntity<ApiResponse<?>> processPayment(@RequestParam Long orderId) {
        try {
            // 서비스 레이어 호출
            Payment payment = paymentService.isPaymentValid(orderId);
            ApiResponse<?> response = paymentService.processPayment(payment);
            return ResponseEntity.ok(response);

        } catch (BusinessException e) {
            // 서비스에서 발생한 예외 처리
            ApiResponse<?> errorResponse = ApiResponse.createException(
                    e.getCode(),
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // 기타 예외 처리 (예상치 못한 에러)
            ApiResponse<?> errorResponse = ApiResponse.createException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "시스템 에러가 발생했습니다. 잠시 후 다시 시도해주세요."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
