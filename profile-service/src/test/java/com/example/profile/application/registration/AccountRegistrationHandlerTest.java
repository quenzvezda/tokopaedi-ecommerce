package com.example.profile.application.registration;

import com.example.common.messaging.AccountRegisteredEvent;
import com.example.profile.application.profile.ProfileCommands;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountRegistrationHandlerTest {

    @Test
    void handle_createsInitialProfile() {
        ProfileCommands commands = mock(ProfileCommands.class);
        AccountRegistrationHandler handler = new AccountRegistrationHandler(commands);

        var event = new AccountRegisteredEvent(UUID.randomUUID(), "alice", "a@x.io", "Alice", "+628123");

        handler.handle(event);

        verify(commands).createInitialProfile(eq(event.accountId()),
                argThat(cmd -> cmd.fullName().equals("Alice") && cmd.phone().equals("+628123")));
    }

    @Test
    void handle_blankPhone_setsNull() {
        ProfileCommands commands = mock(ProfileCommands.class);
        AccountRegistrationHandler handler = new AccountRegistrationHandler(commands);

        var event = new AccountRegisteredEvent(UUID.randomUUID(), "alice", "a@x.io", "Alice", "   ");

        handler.handle(event);

        verify(commands).createInitialProfile(eq(event.accountId()),
                argThat(cmd -> cmd.phone() == null));
    }
}
