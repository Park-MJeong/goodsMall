package com.hanghae.orderservice.event;

public enum PaymentStatus {
    PROCESSING,  // 결제중
    COMPLETE,    // 결제 완료
    CANCELED,    // 결제 취소 (고객이 취소)
    FAILED      // 결제 실패 (결제중에서 시간초과)

}
