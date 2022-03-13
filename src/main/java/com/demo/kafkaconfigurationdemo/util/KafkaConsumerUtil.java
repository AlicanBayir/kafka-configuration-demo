package com.demo.kafkaconfigurationdemo.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter;
import org.springframework.kafka.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.converter.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Duration;
import java.util.Optional;

@Component
public class KafkaConsumerUtil {

    public <T> ConsumerFactory<String, T> createConsumerFactory(Consumer consumer, Class<T> classT) {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);

        var objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ErrorHandlingDeserializer<String> keyDeserializer = new ErrorHandlingDeserializer<>(new StringDeserializer());
        var penaltyInvoiceMessageJsonDeserializer = new JsonDeserializer<>(classT, objectMapper);
        penaltyInvoiceMessageJsonDeserializer.setTypeMapper(typeMapper);
        var valueDeserializer = new ErrorHandlingDeserializer<>(penaltyInvoiceMessageJsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(consumer.getProps(), keyDeserializer, valueDeserializer);
    }

    public RetryTemplate createRetryTemplate(Consumer consumer) {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(consumer.getBackoffIntervalMillis());
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(consumer.getRetryCount() + 1);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    public <T> ConcurrentKafkaListenerContainerFactory<String, T> createSingleKafkaListenerContainerFactory(
            KafkaOperations<String, Object> kafkaOperations,
            ConsumerFactory<String, T> consumerFactory,
            Consumer consumer,
            RetryTemplate retryTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setMissingTopicsFatal(Optional.ofNullable(consumer.getMissingTopicAlertEnable()).orElse(false));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.getContainerProperties().setSyncCommits(Optional.ofNullable(consumer.getSyncCommit()).orElse(true));
        factory.getContainerProperties().setSyncCommitTimeout(Duration.ofSeconds(Optional.ofNullable(consumer.getSyncCommitTimeoutSecond()).orElse(5)));
        factory.setConcurrency(Optional.ofNullable(consumer.getConcurrency()).orElse(1));
        factory.setBatchListener(false);
        factory.setAutoStartup(Optional.ofNullable(consumer.getAutoStartup()).orElse(true));

        if (consumer.getRetryCount() >= 0) {
            factory.setRetryTemplate(retryTemplate);
            addFailoverProcess(kafkaOperations, consumer, factory);
        }

        return factory;
    }

    public Class<?> getDataClass(Consumer consumer) {
        try {
            return Class.forName(consumer.getDataClass());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ConcurrentKafkaListenerContainerFactory<String, ?> createListenerFactory(KafkaOperations<String, Object> kafkaOperations,
                                                                                    Consumer consumer,
                                                                                    ConsumerFactory<String, ?> consumerFactory,
                                                                                    ApplicationContext applicationContext) {
        RetryTemplate retryTemplate = createRetryTemplate(consumer);
        ConcurrentKafkaListenerContainerFactory<String, ?> singleKafkaListenerContainerFactory =
                createSingleKafkaListenerContainerFactory(kafkaOperations, consumerFactory,
                        consumer, retryTemplate);
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        beanFactory.registerSingleton(consumer.getFactoryBeanName(), singleKafkaListenerContainerFactory);
        return singleKafkaListenerContainerFactory;
    }

    private <T> void addFailoverProcess(KafkaOperations<String, Object> kafkaOperations, Consumer consumer, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        if (Optional.ofNullable(consumer.getErrorTopic()).isPresent() || Optional.ofNullable(consumer.getFailoverHandlerBeanName()).isPresent()) {
            ErrorHandler errorHandler = new SeekToCurrentErrorHandler(new FixedBackOff(consumer.getBackoffIntervalMillis(),
                    consumer.getRetryCount() + 1));

            factory.setRecoveryCallback(context -> {
                ConsumerRecord record = (ConsumerRecord) context.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD);
                failoverProcessCustom(context, consumer, record);
                failoverProcessKafka(kafkaOperations, consumer, record);
                return Optional.empty();
            });

            factory.setErrorHandler(errorHandler);
        }
    }

    private void failoverProcessKafka(KafkaOperations<String, Object> kafkaOperations, Consumer consumer, ConsumerRecord record) {
        Optional.ofNullable(consumer.getFailoverHandlerBeanName())
                .ifPresent(_any -> kafkaOperations.send(consumer.getErrorTopic(), record.key().toString(), record.value()));
    }

    private void failoverProcessCustom(RetryContext context, Consumer consumer, ConsumerRecord record) {
        Optional.ofNullable(consumer.getErrorTopic())
                .ifPresent(_any -> {
                    FailoverHandler failoverService = SpringContext.context.getBean(consumer.getFailoverHandlerBeanName(), FailoverHandler.class);
                    failoverService.handle(record, Optional.ofNullable(context.getLastThrowable()).flatMap(t -> Optional.ofNullable(t.getCause())).orElse(null));
                });
    }
}
