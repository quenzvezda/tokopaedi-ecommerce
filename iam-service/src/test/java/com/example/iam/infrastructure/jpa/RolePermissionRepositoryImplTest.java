package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.assignment.RolePermissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(RolePermissionRepositoryImpl.class)
class RolePermissionRepositoryImplTest extends BaseJpaSliceTest {

	RolePermissionRepository repo;

	RolePermissionRepositoryImplTest(RolePermissionRepository repo) {
		this.repo = repo;
	}

	@Test
	void add_exists_remove() {
		repo.add(1L, 200L);
		assertThat(repo.exists(1L, 200L)).isTrue();

		repo.remove(1L, 200L);
		assertThat(repo.exists(1L, 200L)).isFalse();
	}

	@Test
	void findPermissionIds() {
		repo.add(1L, 200L);
		repo.add(1L, 201L);
		repo.add(2L, 200L);

		assertThat(repo.findPermissionIdsByRoleId(1L))
				.containsExactlyInAnyOrder(200L, 201L);

		assertThat(repo.findPermissionIdsByRoleIds(List.of(1L, 2L)))
				.containsExactlyInAnyOrder(200L, 201L);

		assertThat(repo.findPermissionIdsByRoleIds(List.of())).isEmpty();
		assertThat(repo.findPermissionIdsByRoleIds(null)).isEmpty();
	}
}
