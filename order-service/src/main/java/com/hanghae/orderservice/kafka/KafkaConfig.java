package com.hanghae.orderservice.kafka;

import com.hanghae.common.kafka.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.EndpointHandlerMethod;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS;
import static org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS;

@Configuration
@Slf4j
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;


    @Bean
    public ConsumerFactory<String,Object> consumerFactory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        config.put(GROUP_ID_CONFIG,consumerGroupId);
        config.put(KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.hanghae.common.kafka");
//        config.put(MAX_POLL_RECORDS_CONFIG,500);
//        config.put(MAX_POLL_INTERVAL_MS_CONFIG,3000);
//        config.put(FETCH_MAX_WAIT_MS_CONFIG,500);
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEvent.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setIdleBetweenPolls(500); //poll사이의 최대 대기 시간
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE); // 수동 커밋
        return factory;
    }

//    @Bean
//    public RetryTopicConfiguration retryTopicConfiguration(KafkaTemplate<String, Object> kafkaTemplate,
//                                                           ConcurrentKafkaListenerContainerFactory<String, Object> batchFactory) {
//        return RetryTopicConfigurationBuilder
//                .newInstance()
//                .fixedBackOff(3000L)
//                .maxAttempts(3)
//                .listenerFactory(batchFactory)
////                .dltHandlerMethod(new EndpointHandlerMethod(DltMessageHandler.class, "handleDltMessage"))
//                .create(kafkaTemplate);
//
//    }

//    @Bean
//    public CommonErrorHandler commonErrorHandler(){
//        DeadLetterPublishingRecoverer recover = new DeadLetterPublishingRecoverer(kafkaTemplate());
//        FixedBackOff fixedBackOff = new FixedBackOff(1000L, 2L);
//        return new DefaultErrorHandler(recover,fixedBackOff);
//    }


    @Bean
    public ProducerFactory<String,Object> producerFactory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String,Object> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }
}
