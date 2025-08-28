package com.example.iam.domain.user;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

	@Test
	void of_setsNullId_andFields() {
		var acc = UUID.randomUUID();
		var ur = UserRole.of(acc, 10L);
		assertThat(ur.getId()).isNull();
		assertThat(ur.getAccountId()).isEqualTo(acc);
		assertThat(ur.getRoleId()).isEqualTo(10L);
	}

	@Test
	void valueEquality_includesAllFields() {
		var acc = UUID.randomUUID();
		var a = UserRole.of(acc, 10L);
		var b = UserRole.of(acc, 10L);
		assertThat(a).isEqualTo(b);
	}
}
