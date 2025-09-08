package com.example.catalog.application.category;

import com.example.catalog.domain.category.Category;
import com.example.catalog.domain.category.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoryCommandServiceTest {

    CategoryRepository repo = mock(CategoryRepository.class);
    CategoryCommandService svc = new CategoryCommandService(repo);

    @Test
    void create_assignsDefaultsAndSaves() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Category c = svc.create("C", null, null, 1);
        assertThat(c.getName()).isEqualTo("C");
        assertThat(c.isActive()).isTrue();
        verify(repo).save(any());
    }

    @Test
    void update_mergesFields() {
        UUID id = UUID.randomUUID();
        Category current = Category.builder().id(id).name("C").parentId(null).active(true).sortOrder(1).build();
        when(repo.findById(id)).thenReturn(Optional.of(current));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Category updated = svc.update(id, "C2", UUID.randomUUID(), false, 5);
        assertThat(updated.getName()).isEqualTo("C2");
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getSortOrder()).isEqualTo(5);
    }

    @Test
    void delete_delegates() {
        UUID id = UUID.randomUUID();
        svc.delete(id);
        verify(repo).deleteById(id);
    }
}
