package com.demo.kafkaconfigurationdemo.consumer;

import com.demo.kafkaconfigurationdemo.model.DemoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@DependsOn("kafkaFactories")
public class DemoConsumer {
    private final Logger logger = LoggerFactory.getLogger(DemoConsumer.class);

    @KafkaListener(
            topics = "${kafka-config.consumers[demo-consumer].topic}",
            groupId = "${kafka-config.consumers[demo-consumer].props[group.id]}",
            containerFactory = "${kafka-config.consumers[demo-consumer].factory-bean-name}"
    )
    public void consume(@Payload DemoMessage demoMessage,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                        @Header(KafkaHeaders.OFFSET) Long offset,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        logger.info("DemoMessage consumed with topic: {}, and partition: {}, and offset: {}, {}",
                topic,
                partition,
                offset,
                demoMessage);

        //TODO: open this following block to test failover
//        try {
//            throw new RuntimeException();
//        } catch (Exception exception) {
//            logger.error("DemoMessage an error occurred while processing SellerIdsByPeriodMessage {}, {}", demoMessage, exception);
//            throw new RuntimeException();
//        }
    }
}
