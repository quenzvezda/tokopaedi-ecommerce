package com.example.auth.infrastructure.outbox;

import com.example.auth.infrastructure.jpa.entity.OutboxEventEntity;
import com.example.auth.infrastructure.jpa.repository.JpaOutboxEventRepository;
import com.example.common.messaging.AccountRegisteredEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AccountRegistrationOutboxPublisherTest {

    private static final String TOPIC = "account-registered";
    private static final String DLQ_TOPIC = "account-registered-dlq";

    private JpaOutboxEventRepository outboxRepository;
    private KafkaTemplate<String, AccountRegisteredEvent> kafkaTemplate;
    private ObjectMapper objectMapper;
    private AccountRegistrationOutboxPublisher publisher;

    @BeforeEach
    void setUp() {
        outboxRepository = mock(JpaOutboxEventRepository.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        objectMapper = new ObjectMapper();
        publisher = new AccountRegistrationOutboxPublisher(outboxRepository, kafkaTemplate, objectMapper, TOPIC, DLQ_TOPIC, 3);
        when(outboxRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void publishPending_successfulSend_marksEntityPublished() {
        OutboxEventEntity entity = newOutboxEntity();
        when(outboxRepository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(List.of(entity));
        when(kafkaTemplate.send(anyString(), anyString(), any(AccountRegisteredEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        publisher.publishPending();

        assertThat(entity.getPublishedAt()).isNotNull();
        assertThat(entity.getAttempts()).isZero();
        verify(outboxRepository, times(1)).save(entity);
        ArgumentCaptor<AccountRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(AccountRegisteredEvent.class);
        verify(kafkaTemplate, times(1)).send(eq(TOPIC), eq(entity.getEventKey()), eventCaptor.capture());
        assertThat(eventCaptor.getValue().accountId()).isEqualTo(entity.getAggregateId());
    }

    @Test
    void publishPending_failureIncrementsAttempts() {
        OutboxEventEntity entity = newOutboxEntity();
        when(outboxRepository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(List.of(entity));
        when(kafkaTemplate.send(anyString(), anyString(), any(AccountRegisteredEvent.class)))
                .thenThrow(new RuntimeException("broker down"));

        publisher.publishPending();

        assertThat(entity.getAttempts()).isEqualTo(1);
        assertThat(entity.getPublishedAt()).isNull();
        verify(outboxRepository, times(1)).save(entity);
    }

    @Test
    void publishPending_whenAttemptsExceedMax_routesToDeadLetter() {
        OutboxEventEntity entity = newOutboxEntity();
        entity.setAttempts(2); // maxAttempts - 1
        when(outboxRepository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(List.of(entity));
        when(kafkaTemplate.send(anyString(), anyString(), any(AccountRegisteredEvent.class)))
                .thenThrow(new RuntimeException("primary topic down"))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        publisher.publishPending();

        assertThat(entity.getAttempts()).isEqualTo(3);
        assertThat(entity.getPublishedAt()).isNotNull();
        verify(kafkaTemplate, times(2)).send(anyString(), anyString(), any(AccountRegisteredEvent.class));
        ArgumentCaptor<OutboxEventEntity> captor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(outboxRepository, atLeastOnce()).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(OutboxEventEntity::getPublishedAt).contains(entity.getPublishedAt());
    }

    private OutboxEventEntity newOutboxEntity() {
        AccountRegisteredEvent event = new AccountRegisteredEvent(UUID.randomUUID(), "alice", "a@x.io", "Alice", null);
        try {
            String payload = objectMapper.writeValueAsString(event);
            return OutboxEventEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("account")
                    .aggregateId(event.accountId())
                    .eventType("auth.account.registered")
                    .eventKey(event.accountId().toString())
                    .payload(payload)
                    .createdAt(Instant.now())
                    .attempts(0)
                    .build();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
