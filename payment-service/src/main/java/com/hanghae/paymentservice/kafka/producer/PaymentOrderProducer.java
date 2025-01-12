package com.hanghae.paymentservice.kafka.producer;

import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j(topic = "paymentOrderProducer")
@Component
@RequiredArgsConstructor
public class PaymentOrderProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    //    재고부족
    public void stockNotAvailable(OrderEvent orderEvent){
        kafkaTemplate.send("stockNotAvailable", orderEvent);
    }

//    결제 실패
    public void failurePayment(PaymentEvent paymentEvent){
        kafkaTemplate.send("failurePayment", paymentEvent);
    }

//    결제 성공
    public void successPayment(PaymentEvent paymentEvent){
        kafkaTemplate.send("successPayment", paymentEvent);
    }

}
