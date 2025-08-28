package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.user.UserRole;
import com.example.iam.domain.user.UserRoleRepository;
import com.example.iam.infrastructure.jpa.repository.JpaUserRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(UserRoleRepositoryImpl.class)
class UserRoleRepositoryImplTest extends BaseJpaSliceTest {

	UserRoleRepository repo;

	UserRoleRepositoryImplTest(JpaUserRoleRepository jpa) {
		this.repo = new UserRoleRepositoryImpl(jpa);
	}

	@Test
	void add_exists_remove() {
		var acc = UUID.randomUUID();
		repo.add(acc, 10L);
		assertThat(repo.exists(acc, 10L)).isTrue();

		repo.remove(acc, 10L);
		assertThat(repo.exists(acc, 10L)).isFalse();
	}

	@Test
	void findByAccountId_findByRoleId_findRoleIdsByAccountId() {
		var a1 = UUID.randomUUID();
		var a2 = UUID.randomUUID();

		repo.add(a1, 1L);
		repo.add(a1, 2L);
		repo.add(a2, 2L);

		assertThat(repo.findByAccountId(a1))
				.extracting(UserRole::getRoleId)
				.containsExactlyInAnyOrder(1L, 2L);

		assertThat(repo.findByRoleId(2L))
				.extracting(UserRole::getAccountId)
				.contains(a1, a2);

		assertThat(repo.findRoleIdsByAccountId(a1))
				.containsExactlyInAnyOrder(1L, 2L);
	}
}
