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
      ProductCommandService service = new ProductCommandService(repo);

    @Test
      void create_generatesSlug() {
          when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
          var p = service.create("Acme Phone X", "desc", UUID.randomUUID(), UUID.randomUUID(), true);
          assertThat(p.getSlug()).isEqualTo("acme-phone-x");
      }

      @Test
      void create_slugCollision_appendsIndex() {
          var existing = Product.builder().id(UUID.randomUUID()).build();
          when(repo.findBySlug("acme-phone-x")).thenReturn(Optional.of(existing));
          when(repo.findBySlug("acme-phone-x-1")).thenReturn(Optional.empty());
          when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
          var p = service.create("Acme Phone X", "desc", UUID.randomUUID(), UUID.randomUUID(), true);
          assertThat(p.getSlug()).isEqualTo("acme-phone-x-1");
      }

    @Test
      void update_updatesSlug() {
          UUID id = UUID.randomUUID();
          var existing = Product.builder().id(id).name("Old").slug("old").shortDesc("d")
                  .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                  .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
          when(repo.findById(id)).thenReturn(Optional.of(existing));
          when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
          var updated = service.update(id, "New Name", null, null, null, null);
          assertThat(updated.getSlug()).isEqualTo("new-name");
      }

      @Test
      void update_optionalFieldsUpdated() {
          UUID id = UUID.randomUUID();
          UUID brand2 = UUID.randomUUID();
          UUID cat2 = UUID.randomUUID();
          var existing = Product.builder().id(id).name("Name").slug("name").shortDesc("d")
                  .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                  .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
          when(repo.findById(id)).thenReturn(Optional.of(existing));
          when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
          var updated = service.update(id, null, "newd", brand2, cat2, false);
          assertThat(updated.getSlug()).isEqualTo("name");
          assertThat(updated.getShortDesc()).isEqualTo("newd");
          assertThat(updated.getBrandId()).isEqualTo(brand2);
          assertThat(updated.getCategoryId()).isEqualTo(cat2);
          assertThat(updated.isPublished()).isFalse();
      }
}
