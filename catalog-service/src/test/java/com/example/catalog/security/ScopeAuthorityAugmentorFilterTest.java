package com.example.catalog.security;

import com.example.catalog.application.authz.EntitlementsQuery;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ScopeAuthorityAugmentorFilterTest {

    EntitlementsQuery eq = mock(EntitlementsQuery.class);
    ScopeAuthorityAugmentorFilter filter = new ScopeAuthorityAugmentorFilter(eq);

    @Test
    void augment_addsAuthorities() throws Exception {
        UUID id = UUID.randomUUID();
        when(eq.findAuthorities(id, 1)).thenReturn(List.of(new SimpleGrantedAuthority("a")));
        Jwt jwt = Jwt.withTokenValue("t").header("alg","none")
                .claim("sub", id.toString()).claim("perm_ver",1).build();
        Authentication auth = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_U")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filter.doFilterInternal(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        Authentication augmented = SecurityContextHolder.getContext().getAuthentication();
        assertThat(augmented.getAuthorities()).extracting(Object::toString)
                .contains("a", "ROLE_U");
    }

    @Test
    void invalidSub_skipsFetch() throws Exception {
        Jwt jwt = Jwt.withTokenValue("t").header("alg","none")
                .claim("sub", "x").build();
        Authentication auth = new JwtAuthenticationToken(jwt, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        filter.doFilterInternal(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());
        verify(eq, never()).findAuthorities(any(), any());
    }
}

