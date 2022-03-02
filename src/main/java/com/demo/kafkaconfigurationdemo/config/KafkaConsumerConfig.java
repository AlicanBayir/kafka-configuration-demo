package com.demo.kafkaconfigurationdemo.config;

import com.demo.kafkaconfigurationdemo.util.Consumer;
import com.demo.kafkaconfigurationdemo.util.KafkaConsumerUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class KafkaConsumerConfig {
    private final KafkaConsumerUtil kafkaConsumerUtil;
    private final KafkaProducerConsumerProps kafkaProducerConsumerProps;
    private final ApplicationContext applicationContext;

    public KafkaConsumerConfig(KafkaConsumerUtil kafkaConsumerUtil,
                               KafkaProducerConsumerProps kafkaProducerConsumerProps,
                               ApplicationContext applicationContext) {
        this.kafkaConsumerUtil = kafkaConsumerUtil;
        this.kafkaProducerConsumerProps = kafkaProducerConsumerProps;
        this.applicationContext = applicationContext;
    }

    @Bean
    public Map<String, ConcurrentKafkaListenerContainerFactory<?, ?>> kafkaFactories(KafkaOperations<String, Object> kafkaOperations) {
        return kafkaProducerConsumerProps.getConsumers()
                .entrySet()
                .stream()
                .map(entry -> {
                    Consumer consumer = entry.getValue();
                    Class<?> consumerClass = kafkaConsumerUtil.getDataClass(consumer);
                    ConsumerFactory<String, ?> consumerFactory = kafkaConsumerUtil.createConsumerFactory(consumer, consumerClass);
                    return Pair.of(entry.getKey(), kafkaConsumerUtil.createListenerFactory(kafkaOperations, consumer, consumerFactory, applicationContext));
                }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }
}
