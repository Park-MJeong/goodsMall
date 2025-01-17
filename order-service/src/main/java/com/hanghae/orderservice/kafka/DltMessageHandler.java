package com.hanghae.orderservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@Slf4j
@Component
public class DltMessageHandler {
    public void handleDltMessage(List<ConsumerRecord<String, Object>> records) {
        records.forEach(record ->
                log.error("Message moved to DLT: key={}, value={}", record.key(), record.value())
        );
    }
}