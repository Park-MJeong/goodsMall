package com.hanghae.productservice.kafka.consumer;


import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j(topic = "paymentProductConsumer")
@Component
@RequiredArgsConstructor
public class PaymentProductConsumer {
    private final ProductService productService;

    //    결제 성공
    @KafkaListener(topics = "successPayment",groupId = "payment-product")
    public void successPayment(OrderEvent orderEvent) {
        try{
            log.info("[ 성공 ] received successPayment:orderId= {}", orderEvent.getOrderId());
            productService.decreaseStock(orderEvent);
        }catch (Exception e){
            throw new RuntimeException("[ 실패 ] received successPayment:orderId"+orderEvent.getOrderId(), e);
        }
    }
}
