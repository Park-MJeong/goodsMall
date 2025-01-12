package com.hanghae.paymentservice.kafka.consumer;

import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j(topic = "orderPaymentConsumer")
@Component
@RequiredArgsConstructor
public class OrderPaymentConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = "createOrder",groupId = "order-payment")
    public void lister(OrderEvent orderEvent) {
        try{
            log.info("[ 성공 ] received createOrder:orderId= {}", orderEvent.getOrderId());
            paymentService.initPayment(orderEvent);
        }catch (Exception e){

            log.info("[ 재고 부족, 결제 테이블 생성 실패 ]");
            paymentService.stockNotAvailable(orderEvent);
            throw new RuntimeException("received createOrder:orderId"+orderEvent.getOrderId(), e);
        }

    }
}
