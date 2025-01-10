package com.hanghae.orderservice.kafka.consumer;

import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j(topic = "paymentOrderConsumer")
@Component
@RequiredArgsConstructor
public class PaymentOrderConsumer {
    private final OrderService orderService;

    @KafkaListener(topics = "failurePayment",groupId = "payment-order")
    public void lister(OrderEvent orderEvent) {
        try{
            log.info("received failurePayment:orderId= {}", orderEvent.getOrderId());
            orderService.failOrder(orderEvent.getOrderId());
        }catch (Exception e){
            throw new RuntimeException("received failurePayment:orderId"+orderEvent.getOrderId(), e);
        }

    }

}
