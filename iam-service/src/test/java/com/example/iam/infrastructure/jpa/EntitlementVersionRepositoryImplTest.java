package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(EntitlementVersionRepositoryImpl.class)
class EntitlementVersionRepositoryImplTest extends BaseJpaSliceTest {

	EntitlementVersionRepository repo;

	public EntitlementVersionRepositoryImplTest(EntitlementVersionRepository repo) {
		this.repo = repo;
	}

	@Test
	void getOrInit_returns1_thenSame() {
		var acc = UUID.randomUUID();
		assertThat(repo.getOrInit(acc)).isEqualTo(1);
		assertThat(repo.getOrInit(acc)).isEqualTo(1);
	}

	@Test
	void bump_createsOrIncrements() {
		var acc = UUID.randomUUID();
		repo.bump(acc);                 // tidak ada row → dibuat v=1 lalu ++ → 2
		assertThat(repo.getOrInit(acc)).isEqualTo(2);

		repo.bump(acc);                 // 2 → 3
		assertThat(repo.getOrInit(acc)).isEqualTo(3);
	}
}
