package com.example.auth.infrastructure.outbox;

import com.example.auth.infrastructure.jpa.entity.OutboxEventEntity;
import com.example.auth.infrastructure.jpa.repository.JpaOutboxEventRepository;
import com.example.common.messaging.AccountRegisteredEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class AccountRegistrationOutboxPublisher {

    private static final int BATCH_SIZE = 100;

    private final JpaOutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, AccountRegisteredEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;
    private final String deadLetterTopic;
    private final int maxAttempts;

    @Scheduled(fixedDelayString = "${auth.registration.account-registered.publish-interval-ms:1000}")
    public void publishPending() {
        List<OutboxEventEntity> batch = outboxRepository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc();
        if (batch.isEmpty()) {
            return;
        }
        for (OutboxEventEntity entity : batch) {
            try {
                AccountRegisteredEvent event = objectMapper.readValue(entity.getPayload(), AccountRegisteredEvent.class);
                kafkaTemplate.send(topic, entity.getEventKey(), event).get();
                entity.setPublishedAt(Instant.now());
                outboxRepository.save(entity);
                log.debug("Published account registration outbox id={} accountId={}",
                        entity.getId(), entity.getAggregateId());
            } catch (Exception ex) {
                handleFailure(entity, ex);
            }
        }
    }

    private void handleFailure(OutboxEventEntity entity, Exception failure) {
        int attempts = entity.getAttempts() + 1;
        entity.setAttempts(attempts);
        if (attempts >= maxAttempts && deadLetterTopic != null && !deadLetterTopic.isBlank()) {
            moveToDeadLetter(entity, failure);
        } else {
            outboxRepository.save(entity);
            log.warn("Failed to publish account registration outbox id={} attempts={}",
                    entity.getId(), attempts, failure);
        }
    }

    private void moveToDeadLetter(OutboxEventEntity entity, Exception failure) {
        try {
            AccountRegisteredEvent event = objectMapper.readValue(entity.getPayload(), AccountRegisteredEvent.class);
            kafkaTemplate.send(deadLetterTopic, entity.getEventKey(), event).get();
            log.error("Routed account registration outbox id={} to DLQ after {} attempts", entity.getId(), entity.getAttempts(), failure);
        } catch (Exception dlqFailure) {
            log.error("Failed to publish account registration outbox id={} to DLQ", entity.getId(), dlqFailure);
        } finally {
            entity.setPublishedAt(Instant.now());
            outboxRepository.save(entity);
        }
    }
}

