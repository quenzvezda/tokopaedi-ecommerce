package com.example.profile.infrastructure.kafka;

import com.example.common.messaging.AccountRegisteredEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, AccountRegisteredEvent> accountRegisteredConsumerFactory(
            KafkaProperties kafkaProperties) {
        var props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        JsonDeserializer<AccountRegisteredEvent> deserializer = new JsonDeserializer<>(AccountRegisteredEvent.class);
        deserializer.addTrustedPackages(AccountRegisteredEvent.class.getPackageName());
        deserializer.setRemoveTypeHeaders(true);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public DeadLetterPublishingRecoverer profileDeadLetterPublishingRecoverer(KafkaTemplate<Object, Object> template) {
        return new DeadLetterPublishingRecoverer(template);
    }

    @Bean
    public DefaultErrorHandler profileKafkaErrorHandler(DeadLetterPublishingRecoverer recoverer) {
        var backOff = new FixedBackOff(500L, 3);
        return new DefaultErrorHandler(recoverer, backOff);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountRegisteredEvent> accountRegisteredKafkaListenerContainerFactory(
            ConsumerFactory<String, AccountRegisteredEvent> accountRegisteredConsumerFactory,
            DefaultErrorHandler profileKafkaErrorHandler) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, AccountRegisteredEvent>();
        factory.setConsumerFactory(accountRegisteredConsumerFactory);
        factory.setCommonErrorHandler(profileKafkaErrorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
