package com.hanghae.paymentservice.kafka.producer;

import com.hanghae.common.kafka.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j(topic = "paymentProductProducer")
@Component
@RequiredArgsConstructor
public class PaymentProductProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void successPayment(Long paymentId, Long productId, Integer quantity){
        log.info("successPaymentEvent {}",paymentId);
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .paymentId(paymentId)
                .productId(productId)
                .quantity(quantity)
                .build();
        kafkaTemplate.send("successPayment", paymentEvent);
    }
}
