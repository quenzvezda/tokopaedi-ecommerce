package com.example.iam.domain.permission;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionTest {

	@Test
	void ofNew_hasNullId_andFieldsSet() {
		var p = Permission.ofNew("READ_USER", "Can read user");
		assertThat(p.getId()).isNull();
		assertThat(p.getName()).isEqualTo("READ_USER");
		assertThat(p.getDescription()).isEqualTo("Can read user");
	}

	@Test
	void valueSemantics_andWithers() {
		var p1 = Permission.ofNew("READ_USER", "desc");
		var p2 = p1.withDescription("desc");
		var p3 = p1.withDescription("changed");

		assertThat(p1).isEqualTo(p2);
		assertThat(p1).isNotEqualTo(p3);
		assertThat(p3.getName()).isEqualTo("READ_USER");
	}
}
