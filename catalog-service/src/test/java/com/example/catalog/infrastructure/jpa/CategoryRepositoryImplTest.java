package com.example.catalog.infrastructure.jpa;

import com.example.catalog.domain.category.Category;
import com.example.catalog.infrastructure.jpa.entity.JpaCategory;
import com.example.catalog.infrastructure.jpa.repository.JpaCategoryRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoryRepositoryImplTest {

    JpaCategoryRepository jpa = mock(JpaCategoryRepository.class);
    CategoryRepositoryImpl repo = new CategoryRepositoryImpl(jpa);

    @Test
    void save_mapsAndDelegates() {
        UUID id = UUID.randomUUID();
        when(jpa.save(any())).thenReturn(JpaCategory.builder().id(id).name("C").active(true).build());
        Category res = repo.save(Category.builder().id(id).name("C").active(true).build());
        assertThat(res.getId()).isEqualTo(id);
        verify(jpa).save(any());
    }

    @Test
    void findByParent_activeNull_callsFindByParentId() {
        UUID pid = UUID.randomUUID();
        when(jpa.findByParentId(pid)).thenReturn(List.of());
        repo.findByParent(pid, null);
        verify(jpa).findByParentId(pid);
    }

    @Test
    void findAll_activeNotNull_callsFindByActive() {
        when(jpa.findByActive(true)).thenReturn(List.of());
        repo.findAll(true);
        verify(jpa).findByActive(true);
    }

    @Test
    void findById_maps() {
        UUID id = UUID.randomUUID();
        when(jpa.findById(id)).thenReturn(Optional.of(JpaCategory.builder().id(id).name("C").active(true).build()));
        Optional<Category> res = repo.findById(id);
        assertThat(res).isPresent();
        assertThat(res.get().getId()).isEqualTo(id);
    }
}

