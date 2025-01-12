package com.hanghae.paymentservice.util;

import com.hanghae.paymentservice.service.PaymentService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpireListener implements MessageListener {
    private final PaymentService paymentService;

    public RedisKeyExpireListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 레디스에 저장된 키가 만료되면 이벤트 발행
     * */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        // 키가 만료되었는지 확인
        if (expiredKey.startsWith("product:payment:")) {
            Long paymentId = Long.parseLong(expiredKey.split(":")[2]);
            paymentService.cancelPayment(paymentId);
        }
    }
}