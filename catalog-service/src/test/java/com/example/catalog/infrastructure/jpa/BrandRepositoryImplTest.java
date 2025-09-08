package com.example.catalog.infrastructure.jpa;

import com.example.catalog.domain.brand.Brand;
import com.example.catalog.infrastructure.jpa.entity.JpaBrand;
import com.example.catalog.infrastructure.jpa.repository.JpaBrandRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BrandRepositoryImplTest {

    JpaBrandRepository jpa = mock(JpaBrandRepository.class);
    BrandRepositoryImpl repo = new BrandRepositoryImpl(jpa);

    @Test
    void save_maps() {
        UUID id = UUID.randomUUID();
        when(jpa.save(any())).thenReturn(JpaBrand.builder().id(id).name("B").active(true).build());
        Brand res = repo.save(Brand.builder().id(id).name("B").active(true).build());
        assertThat(res.getId()).isEqualTo(id);
    }

    @Test
    void findAll_activeNull_callsFindAll() {
        when(jpa.findAll()).thenReturn(List.of());
        repo.findAll(null);
        verify(jpa).findAll();
    }

    @Test
    void findAll_activeNotNull_callsFindByActive() {
        when(jpa.findByActive(false)).thenReturn(List.of());
        repo.findAll(false);
        verify(jpa).findByActive(false);
    }

    @Test
    void findById_maps() {
        UUID id = UUID.randomUUID();
        when(jpa.findById(id)).thenReturn(Optional.of(JpaBrand.builder().id(id).name("B").active(true).build()));
        assertThat(repo.findById(id)).isPresent();
    }
}

