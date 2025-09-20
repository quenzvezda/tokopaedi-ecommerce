package com.example.auth.domain.common;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        long safeTotal = Math.max(0L, totalElements);
        int totalPages = (int) Math.max(1, Math.ceil((double) safeTotal / (double) safeSize));
        return new PageResult<>(content, safePage, safeSize, safeTotal, totalPages);
    }
}
