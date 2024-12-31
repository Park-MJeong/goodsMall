package com.hanghae.orderservice.event;

public enum OrderStatus {
    PROCESSING,  // 주문 처리 중
    COMPLETE,    // 주문 완료
    DELIVERY_NOW,    // 배송 중
    DELIVERY_COMPLETE,   // 배송 완료
    CANCELLED,   // 주문 취소
    RETURN_NOW,     // 반품중
    RETURN_COMPLETE, //반품완료
    RETURN_NOT_ALLOWED // 반품 불가
}
