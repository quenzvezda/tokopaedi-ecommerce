package com.example.catalog.application.product;

import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProductCommandServiceTest {

    ProductRepository repo = mock(ProductRepository.class);
    ProductCommandService service = new ProductCommandService(repo);

    @Test
    void create_generatesSlug() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        UUID creatorId = UUID.randomUUID();
        var p = service.create(creatorId, "Acme Phone X", "desc", UUID.randomUUID(), UUID.randomUUID(), true);
        assertThat(p.getSlug()).isEqualTo("acme-phone-x");
        assertThat(p.getCreatedBy()).isEqualTo(creatorId);
    }

    @Test
    void create_slugCollision_appendsIndex() {
        var existing = Product.builder().id(UUID.randomUUID()).build();
        when(repo.findBySlug("acme-phone-x")).thenReturn(Optional.of(existing));
        when(repo.findBySlug("acme-phone-x-1")).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var p = service.create(UUID.randomUUID(), "Acme Phone X", "desc", UUID.randomUUID(), UUID.randomUUID(), true);
        assertThat(p.getSlug()).isEqualTo("acme-phone-x-1");
    }

    @Test
    void update_updatesSlug() {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var existing = Product.builder().id(id).name("Old").slug("old").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .published(true).createdBy(owner).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var updated = service.update(owner, id, "New Name", null, null, null, null, false);
        assertThat(updated.getSlug()).isEqualTo("new-name");
    }

    @Test
    void update_optionalFieldsUpdated() {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        UUID brand2 = UUID.randomUUID();
        UUID cat2 = UUID.randomUUID();
        var existing = Product.builder().id(id).name("Name").slug("name").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .published(true).createdBy(owner).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var updated = service.update(owner, id, null, "newd", brand2, cat2, false, false);
        assertThat(updated.getSlug()).isEqualTo("name");
        assertThat(updated.getShortDesc()).isEqualTo("newd");
        assertThat(updated.getBrandId()).isEqualTo(brand2);
        assertThat(updated.getCategoryId()).isEqualTo(cat2);
        assertThat(updated.isPublished()).isFalse();
    }

    @Test
    void update_deniesWhenActorNotOwner() {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var existing = Product.builder().id(id).name("Name").slug("name")
                .createdBy(owner)
                .build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        UUID other = UUID.randomUUID();
        assertThatThrownBy(() -> service.update(other, id, "Name", null, null, null, null, false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("owner");
        verify(repo, never()).save(any());
    }

    @Test
    void update_overrideAllowsDifferentActor() {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var existing = Product.builder().id(id).name("Name").slug("name")
                .createdBy(owner)
                .build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID admin = UUID.randomUUID();
        var updated = service.update(admin, id, "New", null, null, null, null, true);
        assertThat(updated.getName()).isEqualTo("New");
    }

    @Test
    void delete_allowsOwner() {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var existing = Product.builder().id(id).createdBy(owner).build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        service.delete(owner, id, false);

        verify(repo).deleteById(id);
    }

    @Test
    void delete_deniesWhenActorNotOwner() {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var existing = Product.builder().id(id).createdBy(owner).build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        UUID other = UUID.randomUUID();
        assertThatThrownBy(() -> service.delete(other, id, false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("owner");
        verify(repo, never()).deleteById(id);
    }

    @Test
    void delete_overrideAllowsDifferentActor() {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var existing = Product.builder().id(id).createdBy(owner).build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        UUID admin = UUID.randomUUID();
        service.delete(admin, id, true);

        verify(repo).deleteById(id);
    }
}
