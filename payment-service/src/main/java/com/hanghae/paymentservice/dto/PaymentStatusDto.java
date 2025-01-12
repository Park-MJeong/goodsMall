package com.hanghae.paymentservice.dto;

import com.hanghae.paymentservice.domain.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentStatusDto {
    private Long orderId;
    private PaymentStatus paymentStatus;
}
