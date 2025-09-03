package com.example.catalog.infrastructure.outbox;

import com.example.catalog.infrastructure.jpa.entity.OutboxEventEntity;
import com.example.catalog.infrastructure.jpa.repository.JpaOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private static final String TOPIC = "catalog-events";

    private final JpaOutboxEventRepository outboxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${app.outbox.publish-interval-ms:1000}")
    public void publishPending() {
        List<OutboxEventEntity> batch = outboxRepo.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc();
        for (OutboxEventEntity e : batch) {
            try {
                kafkaTemplate.send(TOPIC, e.getEventKey(), e.getPayload()).get();
                e.setPublishedAt(Instant.now());
                outboxRepo.save(e);
                log.debug("Published outbox id={} type={}", e.getId(), e.getEventType());
            } catch (Exception ex) {
                e.setAttempts(e.getAttempts() + 1);
                outboxRepo.save(e);
                log.warn("Failed to publish outbox id={} attempts={}", e.getId(), e.getAttempts(), ex);
            }
        }
    }
}

