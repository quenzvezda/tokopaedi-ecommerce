package com.example.catalog.domain.common;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> PageResult<T> of(List<T> content, int page, int size, long total) {
        int totalPages = (int) Math.max(1, Math.ceil((double) total / Math.max(1, size)));
        return new PageResult<>(content, page, size, total, totalPages);
    }
}
