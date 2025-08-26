package com.example.auth.infrastructure.jwt;

import com.example.auth.config.JwtSettings;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderImplTest {

	@Test
	void generateAccessToken_containsCoreClaims() throws ParseException {
		JwtSettings s = new JwtSettings();
		s.setIssuer("http://issuer");
		s.setAudience("tokopaedi-api");
		s.setAccessTtl("PT15M");

		JwtProviderImpl p = new JwtProviderImpl(s);

		UUID sub = UUID.randomUUID();
		String jwt = p.generateAccessToken(sub, List.of("ADMIN"), 3, Instant.now());

		SignedJWT parsed = SignedJWT.parse(jwt);
		assertThat(parsed.getJWTClaimsSet().getSubject()).isEqualTo(sub.toString());
		assertThat(parsed.getJWTClaimsSet().getAudience()).contains("tokopaedi-api");
		assertThat(parsed.getJWTClaimsSet().getClaim("roles")).isEqualTo(List.of("ADMIN"));
		assertThat(parsed.getJWTClaimsSet().getClaim("perm_ver")).isEqualTo(3L);

		Map<String,Object> jwks = p.currentJwks();
		assertThat(jwks).containsKey("keys");
	}
}
