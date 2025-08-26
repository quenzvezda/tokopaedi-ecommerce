package com.example.auth.domain.token;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenValueObjectTest {
	@Test
	void of_setsFields() {
		var id = UUID.randomUUID();
		var acc = UUID.randomUUID();
		var exp = OffsetDateTime.now(ZoneOffset.UTC).plusDays(7);

		var rt = RefreshToken.of(id, acc, exp, false);

		assertThat(rt.getId()).isEqualTo(id);
		assertThat(rt.getAccountId()).isEqualTo(acc);
		assertThat(rt.isRevoked()).isFalse();
	}
}
