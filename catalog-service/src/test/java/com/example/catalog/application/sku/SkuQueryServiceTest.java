package com.example.catalog.application.sku;

import com.example.catalog.domain.sku.SkuRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class SkuQueryServiceTest {

    SkuRepository repo = mock(SkuRepository.class);
    SkuQueryService svc = new SkuQueryService(repo);

    @Test
    void byProduct_delegates() {
        UUID pid = UUID.randomUUID();
        svc.byProduct(pid);
        verify(repo).findByProductId(pid);
    }
}

