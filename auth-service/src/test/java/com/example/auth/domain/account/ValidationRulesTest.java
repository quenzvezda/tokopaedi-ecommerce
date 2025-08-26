package com.example.auth.domain.account;

import com.example.auth.domain.shared.ValidationRules;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationRulesTest {

	@Test
	void usernameRegex_acceptsLettersDigitsUnderscoreDot_3to32() {
		var r = ValidationRules.USERNAME_PATTERN;

		assertThat(r.matcher("abc").matches()).isTrue();
		assertThat(r.matcher("a_b.c1").matches()).isTrue();
		assertThat(r.matcher("a".repeat(32)).matches()).isTrue();

		assertThat(r.matcher("ab").matches()).isFalse();
		assertThat(r.matcher("a".repeat(33)).matches()).isFalse();
		assertThat(r.matcher("bad*char").matches()).isFalse();
		assertThat(r.matcher(" space ").matches()).isFalse();
	}
}
