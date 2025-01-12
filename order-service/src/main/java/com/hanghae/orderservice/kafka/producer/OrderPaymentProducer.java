package com.hanghae.orderservice.kafka.producer;

import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.orderservice.dto.KafkaRequestDto;
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

    public void createOrder(KafkaRequestDto kafkaRequestDto){
        log.info("createOrderProducer 발행 성공 ");
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(kafkaRequestDto.getOrderId())
                .totalPrice(kafkaRequestDto.getTotalPrice())
                .orderRequestDtoList(kafkaRequestDto.getOrderRequestDtoList())
                .build();
        kafkaTemplate.send("createOrder",orderEvent);
    }
}
