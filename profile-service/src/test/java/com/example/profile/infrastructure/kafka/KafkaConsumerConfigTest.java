package com.example.profile.infrastructure.kafka;

import com.example.common.messaging.AccountRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class KafkaConsumerConfigTest {

    private final KafkaConsumerConfig config = new KafkaConsumerConfig();

    @Test
    void accountRegisteredConsumerFactory_configuresJsonDeserializer() {
        KafkaProperties props = new KafkaProperties();
        props.setBootstrapServers(List.of("localhost:9092"));

        ConsumerFactory<String, AccountRegisteredEvent> factory = config.accountRegisteredConsumerFactory(props);

        assertThat(factory).isNotNull();
        assertThat(factory).isInstanceOfSatisfying(
                org.springframework.kafka.core.DefaultKafkaConsumerFactory.class,
                created -> assertThat(created.getValueDeserializer()).isInstanceOf(JsonDeserializer.class)
        );
    }

    @Test
    void accountRegisteredKafkaListenerContainerFactory_setsRecordAckMode() {
        KafkaTemplate<Object, Object> template = mock(KafkaTemplate.class);
        DeadLetterPublishingRecoverer recoverer = config.profileDeadLetterPublishingRecoverer(template);
        DefaultErrorHandler errorHandler = config.profileKafkaErrorHandler(recoverer);

        @SuppressWarnings("unchecked")
        ConsumerFactory<String, AccountRegisteredEvent> consumerFactory =
                (ConsumerFactory<String, AccountRegisteredEvent>) mock(ConsumerFactory.class);

        ConcurrentKafkaListenerContainerFactory<String, AccountRegisteredEvent> factory =
                config.accountRegisteredKafkaListenerContainerFactory(consumerFactory, errorHandler);

        assertThat(factory.getContainerProperties().getAckMode()).isEqualTo(ContainerProperties.AckMode.RECORD);
    }
}
