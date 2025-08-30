package com.example.catalog.application.product;

import com.example.catalog.domain.product.ProductRepository;
import com.example.catalog.domain.product.ProductSearchCriteria;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductQueryServiceTest {

    ProductRepository repo = mock(ProductRepository.class);
    ProductQueryService svc = new ProductQueryService(repo);

    @Test
    void search_delegates() {
        svc.search("q", UUID.randomUUID(), null, 0, 10);
        verify(repo).search(any(ProductSearchCriteria.class));
    }

    @Test
    void getById_callsRepo() {
        UUID id = UUID.randomUUID();
        try {
            svc.getById(id);
        } catch (Exception ignored) {}
        verify(repo).findById(id);
    }
}

