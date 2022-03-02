package com.demo.kafkaconfigurationdemo.config;

import com.demo.kafkaconfigurationdemo.util.Consumer;
import com.demo.kafkaconfigurationdemo.util.Producer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaProducerConsumerProps {
    private Map<String, Consumer> consumers;
    private Producer producer;

    public Map<String, Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(Map<String, Consumer> consumers) {
        this.consumers = consumers;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }
}