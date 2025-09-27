package com.example.auth.application.account;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.account.PasswordHasher;
import com.example.auth.web.error.EmailAlreadyExistsException;
import com.example.auth.web.error.UsernameAlreadyExistsException;
import com.example.common.messaging.AccountRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountCommandServiceTest {

    private AccountRepository repo;
    private PasswordHasher hasher;
    private AccountRegistrationEventPublisher publisher;
    private AccountCommands service;

    @BeforeEach
    void setUp() {
        repo = mock(AccountRepository.class);
        hasher = mock(PasswordHasher.class);
        publisher = mock(AccountRegistrationEventPublisher.class);
        service = new AccountCommandService(repo, hasher, publisher);
    }

    @Test
    void register_success_savesEncodedPassword_andReturnsId() {
        when(repo.findByUsername("alice")).thenReturn(Optional.empty());
        when(repo.findByEmail("a@x.io")).thenReturn(Optional.empty());
        when(hasher.encode("secret")).thenReturn("HASH");

        var saved = Account.of(UUID.randomUUID(), "alice", "a@x.io", "HASH", "ACTIVE", null);
        when(repo.save(any())).thenReturn(saved);

        var id = service.register("alice", "a@x.io", "secret", "Alice", "+62811111111");

        assertThat(id).isEqualTo(saved.getId());

        ArgumentCaptor<Account> cap = ArgumentCaptor.forClass(Account.class);
        verify(repo).save(cap.capture());
        assertThat(cap.getValue().getPasswordHash()).isEqualTo("HASH");

        ArgumentCaptor<AccountRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(AccountRegisteredEvent.class);
        verify(publisher).publish(eventCaptor.capture());
        var event = eventCaptor.getValue();
        assertThat(event.accountId()).isEqualTo(saved.getId());
        assertThat(event.fullName()).isEqualTo("Alice");
        assertThat(event.phone()).isEqualTo("+62811111111");
    }

    @Test
    void register_usernameTaken_throwsApiException() {
        when(repo.findByUsername("alice")).thenReturn(Optional.of(mock(Account.class)));

        assertThatThrownBy(() -> service.register("alice", "a@x.io", "secret", "Alice", null))
                .isInstanceOf(UsernameAlreadyExistsException.class);

        verify(repo, never()).save(any());
        verifyNoInteractions(hasher, publisher);
    }

    @Test
    void register_emailTaken_throwsApiException() {
        when(repo.findByUsername("alice")).thenReturn(Optional.empty());
        when(repo.findByEmail("a@x.io")).thenReturn(Optional.of(mock(Account.class)));

        assertThatThrownBy(() -> service.register("alice", "a@x.io", "secret", "Alice", null))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verifyNoInteractions(publisher);
    }
}
