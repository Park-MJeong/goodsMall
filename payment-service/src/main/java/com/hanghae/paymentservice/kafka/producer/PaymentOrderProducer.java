package com.hanghae.paymentservice.kafka.producer;

import com.hanghae.common.kafka.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j(topic = "paymentOrderProducer")
@Component
@RequiredArgsConstructor
public class PaymentOrderProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    //    재고부족
    public void stockNotAvailable(OrderEvent orderEvent){
        log.info("stockNotAvailable");
        kafkaTemplate.send("stockNotAvailable", orderEvent);
    }

//    결제 실패
    public void failurePayment(Long orderId){
        log.info("failurePayment");
        kafkaTemplate.send("failurePayment", orderId);
    }

//    결제 성공
    public void successPayment(OrderEvent orderEvent){
        log.info("successPayment");
        kafkaTemplate.send("successPayment", orderEvent);
    }

}
