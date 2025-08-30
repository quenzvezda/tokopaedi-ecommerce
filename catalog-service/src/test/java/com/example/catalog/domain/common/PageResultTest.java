package com.example.catalog.domain.common;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResultTest {
    @Test
    void of_calculatesTotalPages() {
        PageResult<Integer> pr = PageResult.of(List.of(1,2,3), 0, 10, 25);
        assertThat(pr.totalPages()).isEqualTo(3);
    }

    @Test
    void of_handlesZeroSize() {
        PageResult<Integer> pr = PageResult.of(List.of(), 0, 0, 5);
        assertThat(pr.totalPages()).isEqualTo(5);
    }
}
