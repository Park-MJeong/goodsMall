package com.hanghae.orderservice.domain.entity;

public enum OrderStatus {
    PROCESSING,  // 주문 처리 중
    COMPLETE,    // 주문 완료
    DELIVERY_NOW,    // 배송 중
    DELIVERY_COMPLETE,   // 배송 완료
    CANCELED,   // 주문 취소
    RETURN_NOW,     // 반품중
    RETURN_COMPLETE, //반품완료
    RETURN_NOT_ALLOWED, // 반품 불가
    FAILED      // 주문 실패(결제 실패시 함께 처리)
}
