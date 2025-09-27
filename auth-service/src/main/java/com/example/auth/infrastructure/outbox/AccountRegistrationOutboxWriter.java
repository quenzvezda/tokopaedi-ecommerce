package com.example.auth.infrastructure.outbox;

import com.example.auth.application.account.AccountRegistrationEventPublisher;
import com.example.auth.infrastructure.jpa.entity.OutboxEventEntity;
import com.example.auth.infrastructure.jpa.repository.JpaOutboxEventRepository;
import com.example.common.messaging.AccountRegisteredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class AccountRegistrationOutboxWriter implements AccountRegistrationEventPublisher {

    private static final String AGGREGATE_TYPE = "account";
    private static final String EVENT_TYPE = "auth.account.registered";

    private final JpaOutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(AccountRegisteredEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            var entity = OutboxEventEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(AGGREGATE_TYPE)
                    .aggregateId(event.accountId())
                    .eventType(EVENT_TYPE)
                    .eventKey(event.accountId().toString())
                    .payload(payload)
                    .createdAt(Instant.now())
                    .attempts(0)
                    .build();
            outboxRepository.save(entity);
            log.debug("Buffered account registered event into outbox id={} accountId={}",
                    entity.getId(), event.accountId());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize account registered event", ex);
        }
    }
}
