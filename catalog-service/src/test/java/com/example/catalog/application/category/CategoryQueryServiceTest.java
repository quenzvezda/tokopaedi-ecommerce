package com.example.catalog.application.category;

import com.example.catalog.domain.category.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class CategoryQueryServiceTest {
    CategoryRepository repo = mock(CategoryRepository.class);
    CategoryQueryService svc = new CategoryQueryService(repo);

    @Test
    void listWithoutParent_callsFindAll() {
        svc.list(true, null);
        verify(repo).findAll(true);
    }

    @Test
    void listWithParent_callsFindByParent() {
        UUID parent = UUID.randomUUID();
        svc.list(true, parent);
        verify(repo).findByParent(parent, true);
    }
}
