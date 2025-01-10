package com.hanghae.orderservice.kafka.producer;

import com.hanghae.common.kafka.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j(topic = "paymentProducer")
@Component
@RequiredArgsConstructor
public class OrderPaymentProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void createOrder(Long orderId, Long productId, Integer quantity, BigDecimal totalPrice){
        log.info("createOrderEvent {}",orderId);
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .build();
        kafkaTemplate.send("createOrder",orderEvent);
    }
}
