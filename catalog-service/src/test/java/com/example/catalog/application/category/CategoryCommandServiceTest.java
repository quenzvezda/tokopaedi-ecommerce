package com.example.catalog.application.category;

import com.example.catalog.domain.category.Category;
import com.example.catalog.domain.category.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryCommandServiceTest {

    CategoryRepository repo = mock(CategoryRepository.class);
    CategoryCommandService svc = new CategoryCommandService(repo);

    @Test
    void create_savesWithDefaults() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Category c = svc.create("Name", null, null, 5);
        assertThat(c.getName()).isEqualTo("Name");
        assertThat(c.isActive()).isTrue();
        assertThat(c.getSortOrder()).isEqualTo(5);
        verify(repo).save(any());
    }

    @Test
    void update_mergesAndSaves() {
        UUID id = UUID.randomUUID();
        Category existing = Category.builder().id(id).name("A").active(true).sortOrder(1).build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category updated = svc.update(id, "B", null, false, 2);
        assertThat(updated.getName()).isEqualTo("B");
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getSortOrder()).isEqualTo(2);
    }
}

