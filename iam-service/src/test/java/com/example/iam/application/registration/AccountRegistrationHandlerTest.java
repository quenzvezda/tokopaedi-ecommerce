package com.example.iam.application.registration;

import com.example.common.messaging.AccountRegisteredEvent;
import com.example.iam.application.assignment.AssignmentCommands;
import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountRegistrationHandlerTest {

    @Test
    void handle_assignsDefaultRole() {
        AssignmentCommands commands = mock(AssignmentCommands.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        Role role = new Role(10L, "CUSTOMER");
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(role));

        var handler = new AccountRegistrationHandler(commands, roleRepository, "CUSTOMER");
        var event = new AccountRegisteredEvent(UUID.randomUUID(), "alice", "a@x.io", "Alice", null);

        handler.handle(event);

        verify(commands).assignRoleToUser(event.accountId(), 10L);
    }

    @Test
    void handle_missingRole_throws() {
        AssignmentCommands commands = mock(AssignmentCommands.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.empty());

        var handler = new AccountRegistrationHandler(commands, roleRepository, "CUSTOMER");
        var event = new AccountRegisteredEvent(UUID.randomUUID(), "alice", "a@x.io", "Alice", null);

        assertThatThrownBy(() -> handler.handle(event)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(commands);
    }
}
