package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(PermissionRepositoryImpl.class)
class PermissionRepositoryImplTest extends BaseJpaSliceTest {

	PermissionRepository repo;

	public PermissionRepositoryImplTest(PermissionRepository repo) {
		this.repo = repo;
	}

	@Test
	void save_findById_findByName() {
		var saved = repo.save(Permission.ofNew("P_READ", "read"));
		assertThat(saved.getId()).isNotNull();
		assertThat(repo.findById(saved.getId())).contains(saved);
		assertThat(repo.findByName("P_READ")).contains(saved);
	}

	@Test
	void findAll_findAllByIds_findNamesByIds_delete() {
		var a = repo.save(Permission.ofNew("A",""));
		var b = repo.save(Permission.ofNew("B",""));
		var c = repo.save(Permission.ofNew("C",""));

		assertThat(repo.findAll()).hasSize(3);

		assertThat(repo.findAllByIds(List.of(a.getId(), c.getId())))
				.extracting(Permission::getName)
				.containsExactlyInAnyOrder("A","C");

		assertThat(repo.findNamesByIds(List.of(b.getId(), c.getId())))
				.containsExactlyInAnyOrder("B","C");
		assertThat(repo.findNamesByIds(List.of())).isEmpty();
		assertThat(repo.findNamesByIds(null)).isEmpty();

		repo.deleteById(a.getId());
		assertThat(repo.findById(a.getId())).isEmpty();
	}
}
