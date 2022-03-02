package com.demo.kafkaconfigurationdemo.util;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface FailoverHandler {
    void handle(ConsumerRecord consumerRecord, Throwable throwable);
}
