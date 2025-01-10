package com.hanghae.paymentservice.domain.entity;

public enum PaymentStatus {
    PENDING,      //결제 대기중
    PROCESSING,  // 결제중
    COMPLETE,    // 결제 완료
    CANCELED,    // 결제 취소 (고객이 취소)
    FAILED      // 결제 실패 (결제중에서 시간초과)

}
