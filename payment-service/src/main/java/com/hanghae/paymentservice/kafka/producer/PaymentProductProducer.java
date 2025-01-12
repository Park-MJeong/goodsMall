package com.hanghae.paymentservice.kafka.producer;

import com.hanghae.common.kafka.OrderRequestDto;
import com.hanghae.common.kafka.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j(topic = "paymentProductProducer")
@Component
@RequiredArgsConstructor
public class PaymentProductProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void successPayment(Long orderId){
        log.info("successPaymentEvent");
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .orderId(orderId)
                .build();
        kafkaTemplate.send("successPayment", paymentEvent);
    }
}
