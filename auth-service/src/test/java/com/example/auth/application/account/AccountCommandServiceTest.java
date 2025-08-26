package com.example.auth.application.account;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.account.PasswordHasher;
import com.example.auth.web.error.EmailAlreadyExistsException;
import com.example.auth.web.error.UsernameAlreadyExistsException;
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
	private AccountCommands service;

	@BeforeEach
	void setUp() {
		repo = mock(AccountRepository.class);
		hasher = mock(PasswordHasher.class);
		service = new AccountCommandService(repo, hasher);
	}

	@Test
	void register_success_savesEncodedPassword_andReturnsId() {
		when(repo.findByUsername("alice")).thenReturn(Optional.empty());
		when(repo.findByEmail("a@x.io")).thenReturn(Optional.empty());
		when(hasher.encode("secret")).thenReturn("HASH");

		var saved = Account.of(UUID.randomUUID(),"alice","a@x.io","HASH","ACTIVE",null);
		when(repo.save(any())).thenReturn(saved);

		var id = service.register("alice", "a@x.io", "secret");

		assertThat(id).isEqualTo(saved.getId());

		ArgumentCaptor<Account> cap = ArgumentCaptor.forClass(Account.class);
		verify(repo).save(cap.capture());
		assertThat(cap.getValue().getPasswordHash()).isEqualTo("HASH");
	}

	@Test
	void register_usernameTaken_throwsApiException() {
		when(repo.findByUsername("alice")).thenReturn(Optional.of(mock(Account.class)));
		assertThatThrownBy(() -> service.register("alice","a@x.io","secret"))
				.isInstanceOf(UsernameAlreadyExistsException.class);
		verify(repo, never()).save(any());
	}

	@Test
	void register_emailTaken_throwsApiException() {
		when(repo.findByUsername("alice")).thenReturn(Optional.empty());
		when(repo.findByEmail("a@x.io")).thenReturn(Optional.of(mock(Account.class)));
		assertThatThrownBy(() -> service.register("alice","a@x.io","secret"))
				.isInstanceOf(EmailAlreadyExistsException.class);
	}
}
