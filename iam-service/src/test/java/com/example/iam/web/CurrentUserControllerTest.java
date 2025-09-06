package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam.web.dto.CurrentUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CurrentUserControllerTest {

    private EntitlementQueries entitlements;
    private CurrentUserController controller;

    @BeforeEach
    void setUp() {
        entitlements = mock(EntitlementQueries.class);
        controller = new CurrentUserController(entitlements);
    }

    @Test
    void me_returns_id_username_email_roles_permissions() {
        UUID userId = UUID.randomUUID();

        Jwt jwt = Jwt.withTokenValue("t")
                .header("alg", "none")
                .claim("sub", userId.toString())
                .claim("username", "alice")
                .claim("email", "a@x.io")
                .claim("roles", List.of("ADMIN","USER"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600))
                .build();
        Authentication auth = new JwtAuthenticationToken(jwt);

        when(entitlements.getEntitlements(userId)).thenReturn(Map.of(
                "perm_ver", 3,
                "scopes", List.of("product:product:write","SCOPE_catalog:brand:write")
        ));

        CurrentUserResponse out = controller.me(jwt, auth);

        assertThat(out.id()).isEqualTo(userId);
        assertThat(out.username()).isEqualTo("alice");
        assertThat(out.email()).isEqualTo("a@x.io");
        assertThat(out.roles()).containsExactly("ADMIN","USER");
        // normalized to SCOPE_*
        assertThat(out.permissions()).containsExactly("SCOPE_catalog:brand:write","SCOPE_product:product:write");

        verify(entitlements).getEntitlements(userId);
    }

    @Test
    void me_falls_back_roles_from_authorities_when_claim_missing() {
        UUID userId = UUID.randomUUID();

        Jwt jwt = Jwt.withTokenValue("t")
                .header("alg", "none")
                .claim("sub", userId.toString())
                .claim("username", "bob")
                .claim("email", "b@y.io")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600))
                .build();

        // Provide ROLE_* and SCOPE_*; controller will only take ROLE_* for roles
        var auth = new JwtAuthenticationToken(jwt, java.util.List.of(
                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"),
                new org.springframework.security.core.authority.SimpleGrantedAuthority("SCOPE_x:y:z")
        ));

        when(entitlements.getEntitlements(userId)).thenReturn(Map.of("scopes", List.of()));

        CurrentUserResponse out = controller.me(jwt, auth);

        assertThat(out.roles()).containsExactly("ADMIN");
        assertThat(out.permissions()).isEmpty();
    }
}

