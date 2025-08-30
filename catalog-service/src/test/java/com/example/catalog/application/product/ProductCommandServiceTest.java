package com.example.catalog.application.product;

import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductCommandServiceTest {

    ProductRepository repo = mock(ProductRepository.class);
    ProductCommandService svc = new ProductCommandService(repo);

    @Test
    void create_setsTimestampsAndSaves() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Product p = svc.create("P","d",UUID.randomUUID(),UUID.randomUUID(),true);
        assertThat(p.getName()).isEqualTo("P");
        assertThat(p.getCreatedAt()).isNotNull();
        verify(repo).save(any());
    }

    @Test
    void update_mergesFields() {
        UUID id = UUID.randomUUID();
        Product current = Product.builder().id(id).name("P").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .published(false).createdAt(java.time.Instant.now())
                .updatedAt(java.time.Instant.now()).build();
        when(repo.findById(id)).thenReturn(Optional.of(current));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product updated = svc.update(id, "P2", "d2", null, null, true);
        assertThat(updated.getName()).isEqualTo("P2");
        assertThat(updated.getShortDesc()).isEqualTo("d2");
        assertThat(updated.isPublished()).isTrue();
    }
}

