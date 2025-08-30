package com.example.catalog.application.sku;

import com.example.catalog.domain.sku.Sku;
import com.example.catalog.domain.sku.SkuRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SkuCommandServiceTest {

    SkuRepository repo = mock(SkuRepository.class);
    SkuCommandService svc = new SkuCommandService(repo);

    @Test
    void create_saves() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Sku s = svc.create(UUID.randomUUID(), "S", null, null);
        assertThat(s.getSkuCode()).isEqualTo("S");
        assertThat(s.isActive()).isTrue();
        verify(repo).save(any());
    }

    @Test
    void update_merges() {
        UUID id = UUID.randomUUID();
        Sku current = Sku.builder().id(id).productId(UUID.randomUUID()).skuCode("S").active(true).barcode("b").build();
        when(repo.findById(id)).thenReturn(Optional.of(current));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Sku updated = svc.update(id, "S2", false, "b2");
        assertThat(updated.getSkuCode()).isEqualTo("S2");
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getBarcode()).isEqualTo("b2");
    }
}

