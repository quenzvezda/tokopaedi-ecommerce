package com.example.catalog.application.category;

import com.example.catalog.domain.category.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class CategoryQueryServiceTest {

    CategoryRepository repo = mock(CategoryRepository.class);
    CategoryQueryService svc = new CategoryQueryService(repo);

    @Test
    void list_withParent_callsParentRepo() {
        UUID pid = UUID.randomUUID();
        svc.list(true, pid);
        verify(repo).findByParent(pid, true);
    }

    @Test
    void list_withoutParent_callsFindAll() {
        svc.list(null, null);
        verify(repo).findAll(null);
    }
}

