package com.example.catalog.application.brand;

import com.example.catalog.domain.brand.Brand;
import com.example.catalog.domain.brand.BrandRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BrandCommandServiceTest {

    BrandRepository repo = mock(BrandRepository.class);
    BrandCommandService svc = new BrandCommandService(repo);

    @Test
    void create_saves() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Brand b = svc.create("B", null);
        assertThat(b.getName()).isEqualTo("B");
        assertThat(b.isActive()).isTrue();
        verify(repo).save(any());
    }

    @Test
    void update_merges() {
        UUID id = UUID.randomUUID();
        Brand current = Brand.builder().id(id).name("B").active(true).build();
        when(repo.findById(id)).thenReturn(Optional.of(current));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Brand updated = svc.update(id, "B2", false);
        assertThat(updated.getName()).isEqualTo("B2");
        assertThat(updated.isActive()).isFalse();
    }
}

