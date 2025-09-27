package com.example.auth.infrastructure.outbox;

import com.example.auth.infrastructure.jpa.entity.OutboxEventEntity;
import com.example.auth.infrastructure.jpa.repository.JpaOutboxEventRepository;
import com.example.common.messaging.AccountRegisteredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AccountRegistrationOutboxWriterTest {

    private JpaOutboxEventRepository outboxRepository;
    private ObjectMapper objectMapper;
    private AccountRegistrationOutboxWriter writer;

    @BeforeEach
    void setUp() {
        outboxRepository = mock(JpaOutboxEventRepository.class);
        objectMapper = new ObjectMapper();
        writer = new AccountRegistrationOutboxWriter(outboxRepository, objectMapper);
    }

    @Test
    void publish_buffersEventIntoOutbox() {
        UUID accountId = UUID.randomUUID();
        AccountRegisteredEvent event = new AccountRegisteredEvent(accountId, "alice", "a@x.io", "Alice", null);

        writer.publish(event);

        ArgumentCaptor<OutboxEventEntity> captor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(outboxRepository).save(captor.capture());
        OutboxEventEntity entity = captor.getValue();
        assertThat(entity.getAggregateType()).isEqualTo("account");
        assertThat(entity.getAggregateId()).isEqualTo(accountId);
        assertThat(entity.getEventType()).isEqualTo("auth.account.registered");
        assertThat(entity.getEventKey()).isEqualTo(accountId.toString());
        assertThat(entity.getPayload()).contains("\"username\":\"alice\"");
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getAttempts()).isZero();
    }

    @Test
    void publish_whenSerializationFails_throwsIllegalStateException() throws JsonProcessingException {
        ObjectMapper failingMapper = mock(ObjectMapper.class);
        writer = new AccountRegistrationOutboxWriter(outboxRepository, failingMapper);
        when(failingMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom") {});

        AccountRegisteredEvent event = new AccountRegisteredEvent(UUID.randomUUID(), "alice", "a@x.io", "Alice", null);

        assertThatThrownBy(() -> writer.publish(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to serialize");
        verifyNoInteractions(outboxRepository);
    }
}

