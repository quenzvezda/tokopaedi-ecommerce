package com.example.inventory.infrastructure.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class KafkaConsumerConfigTest {

    @Test
    void buildsFactoryWithRecordAckAndErrorHandler() {
        var cfg = new KafkaConsumerConfig();
        KafkaTemplate<Object, Object> template = mock(KafkaTemplate.class);
        DeadLetterPublishingRecoverer dlpr = cfg.deadLetterPublishingRecoverer(template);
        DefaultErrorHandler errorHandler = cfg.defaultErrorHandler(dlpr);

        @SuppressWarnings("unchecked")
        ConsumerFactory<String, String> cf = (ConsumerFactory<String, String>) mock(ConsumerFactory.class);

        ConcurrentKafkaListenerContainerFactory<String, String> factory = cfg.kafkaListenerContainerFactory(cf, errorHandler);

        assertThat(factory).isNotNull();
        assertThat(factory.getContainerProperties().getAckMode()).isEqualTo(ContainerProperties.AckMode.RECORD);
    }
}

