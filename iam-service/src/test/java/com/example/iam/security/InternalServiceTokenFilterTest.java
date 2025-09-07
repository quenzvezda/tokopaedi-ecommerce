package com.example.iam.security;

import com.example.iam.config.InternalAuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

class InternalServiceTokenFilterTest {

    @Test
    void setsInternalRoleWhenHeaderMatches() throws ServletException, IOException {
        var props = new InternalAuthProperties();
        props.setServiceToken("SVC-TOKEN");
        var filter = new InternalServiceTokenFilter(props);

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/internal/v1/ping");
        req.addHeader("X-Internal-Token", "SVC-TOKEN");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        Authentication auth = getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities()).extracting("authority").contains("ROLE_INTERNAL");
    }

    @Test
    void doesNotAuthenticateWhenHeaderMissingOrWrong() throws ServletException, IOException {
        var props = new InternalAuthProperties();
        props.setServiceToken("SVC-TOKEN");
        var filter = new InternalServiceTokenFilter(props);

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/internal/v1/ping");
        req.addHeader("X-Internal-Token", "WRONG");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();

        // Clear context from previous test
        getContext().setAuthentication(null);

        filter.doFilter(req, res, chain);

        assertThat(getContext().getAuthentication()).isNull();
    }
}

