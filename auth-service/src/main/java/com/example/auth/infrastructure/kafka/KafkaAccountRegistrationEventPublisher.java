package com.example.auth.infrastructure.kafka;

import com.example.auth.application.account.AccountRegistrationEventPublisher;
import com.example.common.messaging.AccountRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Kafka-based publisher for {@link AccountRegisteredEvent}.
 */
@RequiredArgsConstructor
public class KafkaAccountRegistrationEventPublisher implements AccountRegistrationEventPublisher {

    private final KafkaTemplate<String, AccountRegisteredEvent> kafkaTemplate;
    private final String topic;
    private final int maxAttempts;
    private final Duration backoff;

    @Override
    public void publish(AccountRegisteredEvent event) {
        int attempt = 0;
        Throwable lastError = null;
        while (attempt < maxAttempts) {
            attempt++;
            try {
                kafkaTemplate.send(topic, event.accountId().toString(), event)
                        .get(30, TimeUnit.SECONDS);
                return;
            } catch (Exception ex) {
                lastError = ex;
                sleepQuietly(backoff);
            }
        }
        if (lastError instanceof RuntimeException runtime) {
            throw runtime;
        }
        throw new IllegalStateException("Failed to publish account registered event", lastError);
    }

    private void sleepQuietly(Duration duration) {
        if (duration == null || duration.isZero()) {
            return;
        }
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
