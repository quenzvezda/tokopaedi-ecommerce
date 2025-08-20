package com.example.catalog.domain.product;

import java.util.UUID;

public record ProductSearchCriteria(
        String q,          // nullable
        UUID brandId,      // nullable
        UUID categoryId,   // nullable
        int page,
        int size
) {
    public String normalizedQuery() {
        return q == null ? "" : q.trim();
    }
}
