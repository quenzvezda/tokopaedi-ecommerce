package com.example.catalog.application.product;

import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductCommandServiceTest {
    ProductRepository repo = mock(ProductRepository.class);
    ProductCommandService svc = new ProductCommandService(repo);

    @Test
    void create_setsDefaultsAndSaves() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Product p = svc.create("P", "desc", UUID.randomUUID(), UUID.randomUUID(), null);
        assertThat(p.getName()).isEqualTo("P");
        assertThat(p.isPublished()).isFalse();
        assertThat(p.getCreatedAt()).isNotNull();
        verify(repo).save(any());
    }

    @Test
    void update_mergesFields() {
        UUID id = UUID.randomUUID();
        Instant initial = Instant.now().minusSeconds(60);
        Product current = Product.builder()
                .id(id)
                .name("P")
                .shortDesc("d")
                .brandId(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
                .published(false)
                .createdAt(initial)
                .updatedAt(initial)
                .build();
        when(repo.findById(id)).thenReturn(Optional.of(current));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID newBrand = UUID.randomUUID();
        Product updated = svc.update(id, "P2", null, newBrand, null, true);
        assertThat(updated.getName()).isEqualTo("P2");
        assertThat(updated.getBrandId()).isEqualTo(newBrand);
        assertThat(updated.isPublished()).isTrue();
        assertThat(updated.getUpdatedAt()).isAfter(initial);
    }

    @Test
    void delete_delegates() {
        UUID id = UUID.randomUUID();
        svc.delete(id);
        verify(repo).deleteById(id);
    }
}
