package com.demo.kafkaconfigurationdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {
    private final KafkaProducerConsumerProps kafkaProducerConsumerProps;

    public KafkaProducerConfig(KafkaProducerConsumerProps kafkaProducerConsumerProps) {
        this.kafkaProducerConsumerProps = kafkaProducerConsumerProps;
    }

    @Bean
    ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConsumerProps.getProducer().getProps());
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    KafkaOperations<String, Object> kafkaOperations() {
        return new KafkaTemplate<>(producerFactory());
    }
}