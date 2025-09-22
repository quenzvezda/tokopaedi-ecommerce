package com.example.auth.web;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerSortNormalizationTest {

    @SuppressWarnings("unchecked")
    private List<String> normalize(Object... values) {
        return (List<String>) ReflectionTestUtils.invokeMethod(UserController.class, "normalizeSortPairs", Arrays.asList(values));
    }

    @Test
    void normalize_ignoresNullAndBlankTokens() {
        var normalized = normalize(null, "   ", "username", "  ");

        assertThat(normalized).containsExactly("username");
    }

    @Test
    void normalize_mergesFieldAndDirectionTokens() {
        var normalized = normalize("username", "DESC");

        assertThat(normalized).containsExactly("username,DESC");
    }

    @Test
    void normalize_respectsCommaSeparatedValues() {
        var normalized = normalize("id,asc", "username", "desc");

        assertThat(normalized).containsExactly("id,asc", "username,desc");
    }

    @Test
    void normalize_fieldWithNonDirectionNextValueKeepsBoth() {
        var normalized = normalize("id", "downwards");

        assertThat(normalized).containsExactly("id", "downwards");
    }

    @Test
    void normalize_unknownFieldLeavesTokensAsIs() {
        var normalized = normalize("email", "desc");

        assertThat(normalized).containsExactly("email", "desc");
    }

    @Test
    void normalize_fieldWithDirectionContainingCommaTreatsSeparately() {
        var normalized = normalize("username", "desc,extra", "asc");

        assertThat(normalized).containsExactly("username", "desc,extra", "asc");
    }
}
