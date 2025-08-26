package com.example.auth.application.auth;

import com.example.auth.application.auth.AuthCommands.TokenPair;
import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.account.PasswordHasher;
import com.example.auth.domain.entitlement.EntitlementClient;
import com.example.auth.domain.entitlement.Entitlements;
import com.example.auth.domain.token.RefreshToken;
import com.example.auth.domain.token.RefreshTokenRepository;
import com.example.auth.domain.token.jwt.JwtProvider;
import com.example.auth.web.error.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthCommandServiceLoginTest {

	private AccountRepository accRepo;
	private PasswordHasher hasher;
	private EntitlementClient iam;
	private JwtProvider jwt;
	private RefreshTokenRepository rt;
	private AuthCommands svc;

	@BeforeEach
	void setUp() {
		accRepo = mock(AccountRepository.class);
		hasher = mock(PasswordHasher.class);
		iam = mock(EntitlementClient.class);
		jwt = mock(JwtProvider.class);
		rt = mock(RefreshTokenRepository.class);
		svc = new AuthCommandService(accRepo, hasher, iam, jwt, rt);
	}

	@Test
	void login_success() {
		var accId = UUID.randomUUID();
		var acc = Account.of(accId,"alice","a@x.io","HASH","ACTIVE", OffsetDateTime.now(ZoneOffset.UTC));
		when(accRepo.findByUsernameOrEmail("alice")).thenReturn(Optional.of(acc));
		when(hasher.matches("secret","HASH")).thenReturn(true);

		when(iam.fetchEntitlements(accId))
				.thenReturn(Entitlements.of(accId, 4, List.of("ADMIN"), Instant.now()));
		when(jwt.generateAccessToken(eq(accId), anyList(), eq(4), any()))
				.thenReturn("jwt");
		when(jwt.getAccessTtlSeconds()).thenReturn(900L);

		var rotated = RefreshToken.of(UUID.randomUUID(), accId,
				OffsetDateTime.now(ZoneOffset.UTC).plusDays(7), false);
		when(rt.create(any(), eq(accId), any())).thenReturn(rotated);

		TokenPair out = svc.login("alice", "secret");

		assertThat(out.tokenType()).isEqualTo("Bearer");
		assertThat(out.accessToken()).isEqualTo("jwt");
		assertThat(out.expiresIn()).isEqualTo(900);
		assertThat(out.refreshToken()).isEqualTo(rotated.getId().toString());
	}

	@Test
	void login_wrongPassword_throws() {
		var acc = Account.of(UUID.randomUUID(),"alice","a@x.io","HASH","ACTIVE", OffsetDateTime.now(ZoneOffset.UTC));
		when(accRepo.findByUsernameOrEmail("alice")).thenReturn(Optional.of(acc));
		when(hasher.matches("bad","HASH")).thenReturn(false);

		assertThatThrownBy(() -> svc.login("alice","bad"))
				.isInstanceOf(InvalidCredentialsException.class);

		verifyNoInteractions(iam, jwt, rt);
	}
}
