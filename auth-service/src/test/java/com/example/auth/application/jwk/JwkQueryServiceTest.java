package com.example.auth.application.jwk;

import com.example.auth.domain.token.jwt.JwtProvider;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwkQueryServiceTest {
	@Test
	void handle_returnsProviderJwks() {
		JwtProvider p = mock(JwtProvider.class);
		when(p.currentJwks()).thenReturn(Map.of("keys", "test"));
		JwkQueries q = new JwkQueryService(p);
		assertThat(q.jwks()).containsEntry("keys", "test");
	}
}
