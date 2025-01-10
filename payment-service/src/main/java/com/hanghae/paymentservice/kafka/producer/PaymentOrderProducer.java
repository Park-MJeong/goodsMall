package com.hanghae.paymentservice.kafka.producer;

import com.hanghae.common.kafka.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j(topic = "paymentOrderProducer")
@Component
@RequiredArgsConstructor
public class PaymentOrderProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void failurePayment(OrderEvent orderEvent){

        kafkaTemplate.send("failurePayment", orderEvent);
    }
}
