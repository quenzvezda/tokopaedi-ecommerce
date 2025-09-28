package com.example.profile.infrastructure.kafka;

import com.example.common.messaging.AccountRegisteredEvent;
import com.example.profile.application.registration.AccountRegistrationHandler;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AccountRegisteredListenerTest {

    private final AccountRegistrationHandler handler = mock(AccountRegistrationHandler.class);
    private final AccountRegisteredListener listener = new AccountRegisteredListener(handler);

    @Test
    void consume_delegatesToHandler() {
        AccountRegisteredEvent event = new AccountRegisteredEvent(
                UUID.randomUUID(),
                "user",
                "user@example.com",
                "User",
                "+628123456789"
        );

        listener.consume(event);

        verify(handler).handle(event);
    }
}
