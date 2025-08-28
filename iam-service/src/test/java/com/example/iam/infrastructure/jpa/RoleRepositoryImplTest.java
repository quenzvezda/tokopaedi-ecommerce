package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.infrastructure.jpa.repository.JpaRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(RoleRepositoryImpl.class)
class RoleRepositoryImplTest extends BaseJpaSliceTest {

	RoleRepository repo;

	RoleRepositoryImplTest(JpaRoleRepository jpa) {
		this.repo = new RoleRepositoryImpl(jpa);
	}

	@Test
	void save_find_delete() {
		var r = repo.save(Role.ofNew("ADMIN"));
		assertThat(r.getId()).isNotNull();
		assertThat(repo.findById(r.getId())).contains(r);
		assertThat(repo.findByName("ADMIN")).contains(r);

		repo.deleteById(r.getId());
		assertThat(repo.findById(r.getId())).isEmpty();
	}

	@Test
	void findAll() {
		repo.save(Role.ofNew("R1"));
		repo.save(Role.ofNew("R2"));
		assertThat(repo.findAll()).hasSize(2);
	}
}
