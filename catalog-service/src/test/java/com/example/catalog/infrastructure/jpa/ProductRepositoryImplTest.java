package com.example.catalog.infrastructure.jpa;

import com.example.catalog.domain.common.PageResult;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductSearchCriteria;
import com.example.catalog.infrastructure.jpa.entity.JpaProduct;
import com.example.catalog.infrastructure.jpa.repository.JpaProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductRepositoryImplTest {

    JpaProductRepository jpa = mock(JpaProductRepository.class);
    ProductRepositoryImpl repo = new ProductRepositoryImpl(jpa);

    @Test
    void save_maps() {
        UUID id = UUID.randomUUID();
        when(jpa.save(any())).thenReturn(JpaProduct.builder().id(id).name("P").shortDesc("d").brandId(UUID.randomUUID()).categoryId(UUID.randomUUID()).published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build());
        Product res = repo.save(Product.builder().id(id).name("P").shortDesc("d").brandId(UUID.randomUUID()).categoryId(UUID.randomUUID()).published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build());
        assertThat(res.getId()).isEqualTo(id);
    }

    @Test
    void findById_maps() {
        UUID id = UUID.randomUUID();
        when(jpa.findById(id)).thenReturn(Optional.of(JpaProduct.builder().id(id).name("P").shortDesc("d").brandId(UUID.randomUUID()).categoryId(UUID.randomUUID()).published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build()));
        assertThat(repo.findById(id)).isPresent();
    }

    @Test
    void search_buildsPageRequest() {
        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        Page<JpaProduct> page = new PageImpl<>(List.of());
        when(jpa.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        PageResult<Product> res = repo.search(new ProductSearchCriteria("q", null, null, -1, 0));

        verify(jpa).findAll(any(Specification.class), captor.capture());
        assertThat(captor.getValue().getPageNumber()).isZero();
        assertThat(captor.getValue().getPageSize()).isEqualTo(1);
        assertThat(res.content()).isEmpty();
    }
}

