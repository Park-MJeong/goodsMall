package com.hanghae.orderservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.orderservice.domain.entity.OrderStatus;
import com.hanghae.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.asm.TypeReference;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "paymentOrderConsumer")
@Component
@RequiredArgsConstructor
public class PaymentOrderConsumer {
    private final OrderService orderService;


//    재고 부족
    @KafkaListener(topics = "stockNotAvailable",groupId = "payment-order",containerFactory = "batchFactory")
    public void stockNotAvailable(List<OrderEvent> orderEvents,Acknowledgment ack) {
        try{
            log.info("[ 재고 부족 수신] received stockNotAvailable");
            List<Long> orderIds = new ArrayList<>();
            for(OrderEvent orderEvent : orderEvents){
                log.info("재고 부족 수신 아이디: "+orderEvent.getOrderId());
                orderIds.add(orderEvent.getOrderId());
            }
            orderService.changeOrderListStatus(orderIds, OrderStatus.FAILED);
            ack.acknowledge();

        }catch (Exception e){
            throw new RuntimeException("[ 재고 부족 수신 처리실패 ] received stockNotAvailable", e);
        }

    }

//    결제 실패
    @KafkaListener(topics = "failurePayment",groupId = "payment-order",containerFactory = "batchFactory")
    public void failurePayment(@Payload List<Long> orderIds, Acknowledgment ack) {
        try{
            log.info("[ 결제 실패 수신 ] received failurePayment");
            orderService.changeOrderListStatus(orderIds, OrderStatus.FAILED);
        }catch (Exception e){
            throw new RuntimeException("[ 결제 실패 수신 처리실패 ]  received failurePayment");
        }finally {
            ack.acknowledge();
        }
    }
//    결제 성공
    @KafkaListener(topics = "successPayment",groupId = "payment-order",containerFactory = "batchFactory")
    public void successPayment(List<OrderEvent> orderEvents, Acknowledgment ack) {
        try{
//            log.info("[ 결제 성공 수신 ] received successPayment:orderId= {}", orderEvent.getOrderId());
            log.info("[ 결제 성공 수신 ] received successPayment");
            List<Long> orderIds = new ArrayList<>();
            for(OrderEvent orderEvent : orderEvents){
                log.info("성공아이디: "+orderEvent.getOrderId());
                orderIds.add(orderEvent.getOrderId());
            }
            orderService.changeOrderListStatus(orderIds, OrderStatus.COMPLETE);
        }catch (Exception e){
            log.info("[ 결제 성공 수신 처리실패 ]  received successPayment");

        }finally {
            ack.acknowledge();
        }
    }

}
