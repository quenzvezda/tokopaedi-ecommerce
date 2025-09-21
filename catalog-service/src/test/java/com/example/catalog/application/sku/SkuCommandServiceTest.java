package com.example.catalog.application.sku;

import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductRepository;
import com.example.catalog.domain.sku.Sku;
import com.example.catalog.domain.sku.SkuRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SkuCommandServiceTest {
    SkuRepository repo = mock(SkuRepository.class);
    ProductRepository productRepository = mock(ProductRepository.class);
    ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    SkuCommandService svc = new SkuCommandService(repo, productRepository, events);

    @Test
    void create_setsDefaultsAndSaves() {
        UUID actorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.of(Product.builder().id(productId).createdBy(actorId).build()));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Sku s = svc.create(actorId, productId, "S", null, null, false);
        assertThat(s.getSkuCode()).isEqualTo("S");
        assertThat(s.isActive()).isTrue();
        verify(repo).save(any());
        // Ensure events published (match the Object overload)
        verify(events, atLeastOnce()).publishEvent(any(Object.class));
    }

    @Test
    void update_mergesFields() {
        UUID id = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Sku current = Sku.builder().id(id).productId(productId).skuCode("S").active(true).build();
        when(repo.findById(id)).thenReturn(Optional.of(current));
        when(productRepository.findById(productId)).thenReturn(Optional.of(Product.builder().id(productId).createdBy(actorId).build()));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Sku updated = svc.update(actorId, id, "S2", false, "b", false);
        assertThat(updated.getSkuCode()).isEqualTo("S2");
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getBarcode()).isEqualTo("b");
    }

    @Test
    void delete_delegates() {
        UUID id = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(Sku.builder().id(id).productId(productId).build()));
        when(productRepository.findById(productId)).thenReturn(Optional.of(Product.builder().id(productId).createdBy(actorId).build()));
        svc.delete(actorId, id, false);
        verify(repo).deleteById(id);
    }

    @Test
    void create_rejectsWhenActorNotOwner() {
        UUID productId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.of(Product.builder().id(productId).createdBy(ownerId).build()));

        assertThatThrownBy(() -> svc.create(other, productId, "S", true, null, false))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}
