package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
	void saveAll_bulkPersist_andHandlesEmptyInput() {
		var created = repo.saveAll(List.of(Permission.ofNew("BULK_A", "a"), Permission.ofNew("BULK_B", "b")));
		assertThat(created).hasSize(2).allSatisfy(p -> assertThat(p.getId()).isNotNull());
		assertThat(repo.findNamesByIds(created.stream().map(Permission::getId).toList()))
				.containsExactlyInAnyOrder("BULK_A", "BULK_B");
		assertThat(repo.saveAll(List.of())).isEmpty();
		assertThat(repo.saveAll(null)).isEmpty();
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

    @Test
    void search_by_q_and_sort_variants() {
        var p1 = repo.save(Permission.ofNew("ORDER_READ", "order read"));
        var p2 = repo.save(Permission.ofNew("USER_EDIT", "edit user"));
        var p3 = repo.save(Permission.ofNew("ORDER_WRITE", "order write"));

        // FE style split: sort=name,ASC -> ["name", "ASC"]
        var pageAsc = repo.search("order", List.of("name", "ASC"), 0, 10);
        assertThat(pageAsc.content()).extracting(Permission::getName)
                .containsExactly("ORDER_READ", "ORDER_WRITE");

        var pageDesc = repo.search("order", List.of("name,desc"), 0, 10);
        assertThat(pageDesc.content()).extracting(Permission::getName)
                .containsExactly("ORDER_WRITE", "ORDER_READ");

        // invalid field
        assertThatThrownBy(() -> repo.search("order", List.of("bogus,asc"), 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid sort field");
    }
}

