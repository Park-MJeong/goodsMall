package com.hanghae.paymentservice.kafka.consumer;

import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j(topic = "orderPaymentConsumer")
@Component
@RequiredArgsConstructor
public class OrderPaymentConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = "createOrder",groupId = "order-payment",containerFactory = "batchFactory")
    public void lister(List<OrderEvent> orderEvents) {
        try{
            log.info("[주문테이블 생성 수신 성공 ] received createOrder");
            paymentService.initPayment(orderEvents);
        }catch (Exception e){
            log.info("[ 재고 부족, 결제 테이블 생성 실패 ]");
            for (OrderEvent orderEvent : orderEvents) {
                paymentService.stockNotAvailable(orderEvent);
            }
//            throw new RuntimeException("received createOrder:orderId"+orderEvent.getOrderId(), e);
        }
    }
}
