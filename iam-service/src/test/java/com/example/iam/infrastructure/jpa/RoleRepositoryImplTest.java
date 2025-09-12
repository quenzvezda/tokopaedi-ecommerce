package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.infrastructure.jpa.repository.JpaRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void search_q_and_sort() {
        repo.save(Role.ofNew("ADMIN"));
        repo.save(Role.ofNew("MANAGER"));
        repo.save(Role.ofNew("READER"));

        // FE CSV split form
        var page = repo.search("man", java.util.List.of("name", "DESC"), 0, 10);
        assertThat(page.content()).extracting(Role::getName).containsExactly("MANAGER");

        var asc = repo.search("", java.util.List.of("name,asc"), 0, 10);
        assertThat(asc.content()).extracting(Role::getName).contains("ADMIN", "MANAGER", "READER");

        assertThatThrownBy(() -> repo.search(null, java.util.List.of("bogus,asc"), 0, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid sort field");
    }
}
