package com.example.iam.domain.role;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

	@Test
	void ofNew_hasNullId_andNameSet() {
		var r = Role.ofNew("ADMIN");
		assertThat(r.getId()).isNull();
		assertThat(r.getName()).isEqualTo("ADMIN");
	}

	@Test
	void valueEquality_byAllFields() {
		var r1 = Role.ofNew("ADMIN");
		var r2 = Role.ofNew("ADMIN");
		var rWithId = r1.withId(1L);

		assertThat(r1).isEqualTo(r2);
		assertThat(r1).isNotEqualTo(rWithId);
	}
}
