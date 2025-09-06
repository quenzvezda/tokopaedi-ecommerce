package com.example.auth.application.auth;

import com.example.auth.application.auth.AuthCommands.TokenPair;
import com.example.auth.domain.entitlement.EntitlementClient;
import com.example.auth.domain.entitlement.Entitlements;
import com.example.auth.domain.token.RefreshToken;
import com.example.auth.domain.token.RefreshTokenRepository;
import com.example.auth.domain.token.jwt.JwtProvider;
import com.example.auth.web.error.RefreshTokenInvalidException;
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

class AuthCommandServiceRefreshTest {

    private RefreshTokenRepository rt;
    private EntitlementClient iam;
    private JwtProvider jwt;
    private com.example.auth.domain.account.AccountRepository accRepo;
    private AuthCommands svc;

	@BeforeEach
	void setUp() {
        rt = mock(RefreshTokenRepository.class);
        iam = mock(EntitlementClient.class);
        jwt = mock(JwtProvider.class);
        accRepo = mock(com.example.auth.domain.account.AccountRepository.class);
        svc = new AuthCommandService(accRepo, null, iam, jwt, rt);
    }

	@Test
	void refresh_success_rotatesToken() {
		var accId = UUID.randomUUID();
		var cur = RefreshToken.of(UUID.randomUUID(), accId, OffsetDateTime.now(ZoneOffset.UTC).plusDays(5), false);
		when(rt.findById(cur.getId())).thenReturn(Optional.of(cur));

		var rotated = RefreshToken.of(UUID.randomUUID(), accId, OffsetDateTime.now(ZoneOffset.UTC).plusDays(7), false);
		when(rt.create(any(), eq(accId), any())).thenReturn(rotated);

		when(iam.fetchEntitlements(accId))
				.thenReturn(Entitlements.of(accId, 4, List.of("ADMIN"), Instant.now()));
        // account lookup for username/email
        var acc = com.example.auth.domain.account.Account.of(accId, "bob", "b@y.io", "H", "ACTIVE", OffsetDateTime.now(ZoneOffset.UTC));
        when(accRepo.findById(accId)).thenReturn(java.util.Optional.of(acc));

        when(jwt.generateAccessToken(eq(accId), anyList(), eq(4), any(), eq("bob"), eq("b@y.io")))
                .thenReturn("newjwt");
		when(jwt.getAccessTtlSeconds()).thenReturn(900L);

        TokenPair out = svc.refresh(cur.getId().toString());

		verify(rt).consume(cur.getId());
		assertThat(out.refreshToken()).isEqualTo(rotated.getId().toString());
        assertThat(out.accessToken()).isEqualTo("newjwt");

        // verify username/email forwarded on refresh
        verify(jwt).generateAccessToken(eq(accId), anyList(), eq(4), any(), eq("bob"), eq("b@y.io"));
	}

	@Test
	void refresh_invalidToken_throws() {
		var id = UUID.randomUUID();
		when(rt.findById(id)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> svc.refresh(id.toString()))
				.isInstanceOf(RefreshTokenInvalidException.class);
	}
}
