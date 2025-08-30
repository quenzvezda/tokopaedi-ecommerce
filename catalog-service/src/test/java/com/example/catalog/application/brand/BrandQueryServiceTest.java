package com.example.catalog.application.brand;

import com.example.catalog.domain.brand.BrandRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class BrandQueryServiceTest {

    BrandRepository repo = mock(BrandRepository.class);
    BrandQueryService svc = new BrandQueryService(repo);

    @Test
    void list_delegates() {
        svc.list(true);
        verify(repo).findAll(true);
    }
}

