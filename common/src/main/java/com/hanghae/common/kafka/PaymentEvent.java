package com.hanghae.common.kafka;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentEvent {
    private Long paymentId;
    private Long productId;
    private Integer quantity;
}
