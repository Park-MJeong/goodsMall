package com.hanghae.orderservice.kafka.consumer;

import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.PaymentEvent;
import com.hanghae.orderservice.domain.entity.OrderStatus;
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


//    재고 부족
    @KafkaListener(topics = "stockNotAvailable",groupId = "payment-order")
    public void stockNotAvailable(OrderEvent orderEvent) {
        try{
            log.info("[ 성공 ] received stockNotAvailable:orderId= {}", orderEvent.getOrderId());
            orderService.changeOrderStatus(orderEvent.getOrderId(), OrderStatus.FAILED);
        }catch (Exception e){
            throw new RuntimeException("[ 실패 ] received stockNotAvailable:orderId"+orderEvent.getOrderId(), e);
        }

    }

//    결제 실패
    @KafkaListener(topics = "failurePayment",groupId = "payment-order")
    public void failurePayment(PaymentEvent payment) {
        try{
            log.info("[ 성공 ] received failurePayment:orderId= {}", payment.getOrderId());
            orderService.changeOrderStatus(payment.getOrderId(), OrderStatus.FAILED);
        }catch (Exception e){
            throw new RuntimeException("[ 실패 ] received failurePayment:orderId"+payment.getOrderId(), e);
        }
    }
//    결제 성공
    @KafkaListener(topics = "failurePayment",groupId = "payment-order")
    public void successPayment(PaymentEvent payment) {
        try{
            log.info("[ 성공 ] received failurePayment:orderId= {}", payment.getOrderId());
            orderService.changeOrderStatus(payment.getOrderId(), OrderStatus.COMPLETE);
        }catch (Exception e){
            throw new RuntimeException("[ 실패 ] received failurePayment:orderId"+payment.getOrderId(), e);
        }
    }

}
