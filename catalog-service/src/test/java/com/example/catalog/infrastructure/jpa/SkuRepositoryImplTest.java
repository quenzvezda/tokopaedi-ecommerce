package com.example.catalog.infrastructure.jpa;

import com.example.catalog.domain.sku.Sku;
import com.example.catalog.infrastructure.jpa.entity.JpaSku;
import com.example.catalog.infrastructure.jpa.repository.JpaSkuRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SkuRepositoryImplTest {

    JpaSkuRepository jpa = mock(JpaSkuRepository.class);
    SkuRepositoryImpl repo = new SkuRepositoryImpl(jpa);

    @Test
    void save_maps() {
        UUID id = UUID.randomUUID();
        when(jpa.save(any())).thenReturn(JpaSku.builder().id(id).productId(UUID.randomUUID()).skuCode("S").active(true).build());
        Sku res = repo.save(Sku.builder().id(id).productId(UUID.randomUUID()).skuCode("S").active(true).build());
        assertThat(res.getId()).isEqualTo(id);
    }

    @Test
    void findByProductId_mapsList() {
        UUID pid = UUID.randomUUID();
        when(jpa.findByProductId(pid)).thenReturn(List.of(JpaSku.builder().id(UUID.randomUUID()).productId(pid).skuCode("S").active(true).build()));
        List<Sku> list = repo.findByProductId(pid);
        assertThat(list).hasSize(1);
        verify(jpa).findByProductId(pid);
    }

    @Test
    void findById_maps() {
        UUID id = UUID.randomUUID();
        when(jpa.findById(id)).thenReturn(Optional.of(JpaSku.builder().id(id).productId(UUID.randomUUID()).skuCode("S").active(true).build()));
        assertThat(repo.findById(id)).isPresent();
    }
}

