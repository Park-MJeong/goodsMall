//package com.hanghae.orderservice.dto.Payment;
//
//import com.hanghae.orderservice.domain.entity.Order;
//import com.hanghae.orderservice.domain.entity.Payment;
//import lombok.Getter;
//
//import java.math.BigDecimal;
//
//@Getter
//public class InitPaymentResponseDto {
//    private long paymentId;
//    private String orderStatus;
//    private String paymentStatus;
//    private BigDecimal paymentPrice;
//
//    public InitPaymentResponseDto(Order order, Payment payment) {
//        this.paymentId = payment.getId();
//        this.orderStatus = order.getStatus().toString();
//        this.paymentStatus = payment.getStatus().toString();
//        this.paymentPrice = order.getTotalPrice();
//
//    }
//
//}
