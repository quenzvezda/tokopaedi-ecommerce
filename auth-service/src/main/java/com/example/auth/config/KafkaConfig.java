package com.example.auth.config;

import com.example.auth.infrastructure.kafka.IamPermissionEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
public class KafkaConfig {
    @Value("${topics.entitlements}") private String topic;
    @Value("${topics.entitlementsDlq}") private String dlq;

    @Bean
    public NewTopic entitlementsTopic() { return TopicBuilder.name(topic).partitions(1).replicas(1).compact().build(); }

    @Bean
    public NewTopic entitlementsDlqTopic() { return TopicBuilder.name(dlq).partitions(1).replicas(1).build(); }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template, (r, e) -> new TopicPartition(dlq, r.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(500L, 2));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, IamPermissionEvent> kafkaListenerContainerFactory(ConsumerFactory<String, IamPermissionEvent> cf,
                                                                                                             DefaultErrorHandler eh) {
        ConcurrentKafkaListenerContainerFactory<String, IamPermissionEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        factory.setCommonErrorHandler(eh);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        return factory;
    }
}
