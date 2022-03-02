package com.demo.kafkaconfigurationdemo.handler;

import com.demo.kafkaconfigurationdemo.consumer.DemoConsumer;
import com.demo.kafkaconfigurationdemo.util.FailoverHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefaultConsumerFailoverHandler implements FailoverHandler {
    private final Logger logger = LoggerFactory.getLogger(DemoConsumer.class);

    @Override
    public void handle(ConsumerRecord record, Throwable throwable) {
        logger.error("Default Consumer Failover Handler Success. Handled Error: ", throwable);
    }
}
