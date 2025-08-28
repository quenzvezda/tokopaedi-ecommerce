package com.example.iam.domain.entitlement;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntitlementVersionTest {

	@Test
	void init_setsVersion1_andNullId() {
		var acc = UUID.randomUUID();
		var v = EntitlementVersion.init(acc);
		assertThat(v.getId()).isNull();
		assertThat(v.getAccountId()).isEqualTo(acc);
		assertThat(v.getVersion()).isEqualTo(1);
	}

	@Test
	void bump_incrementsVersion_andKeepsIdentity() {
		var acc = UUID.randomUUID();
		var v1 = EntitlementVersion.init(acc);
		var v3 = v1.bump().bump();
		assertThat(v1.getVersion()).isEqualTo(1);
		assertThat(v3.getVersion()).isEqualTo(3);
		assertThat(v3.getId()).isEqualTo(v1.getId());
		assertThat(v3.getAccountId()).isEqualTo(acc);
		assertThat(v3).isNotSameAs(v1);
	}

	@Test
	void withers_areImmutable() {
		var acc = UUID.randomUUID();
		var v1 = EntitlementVersion.init(acc);
		var v99 = v1.withVersion(99);
		assertThat(v1.getVersion()).isEqualTo(1);
		assertThat(v99.getVersion()).isEqualTo(99);
		assertThat(v99.getAccountId()).isEqualTo(acc);
	}
}
