package com.example.auth.infrastructure.jwt;

import com.example.auth.config.JwtSettings;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderImplTest {

    @Test
    @SuppressWarnings("unchecked")
    void generateAccessToken_containsCoreClaims_headerKid_andTtl() throws Exception {
        JwtSettings s = new JwtSettings();
        s.setIssuer("http://auth.local");
        s.setAudience("gateway");
        s.setAccessTtl("PT15M");

        JwtProviderImpl p = new JwtProviderImpl(s);

        UUID sub = UUID.randomUUID();
        List<String> roles = List.of("ADMIN", "USER");
        int permVer = 3;
        Instant now = Instant.parse("2025-01-01T00:00:00Z");

        String token = p.generateAccessToken(sub, roles, permVer, now);
        SignedJWT jwt = SignedJWT.parse(token);

        // Header
        assertThat(jwt.getHeader().getAlgorithm().getName()).isEqualTo("RS256");
        assertThat(jwt.getHeader().getType().getType()).isEqualTo("JWT");
        assertThat(jwt.getHeader().getKeyID()).isEqualTo(p.getKid());

        // Claims
        var c = jwt.getJWTClaimsSet();
        assertThat(c.getSubject()).isEqualTo(sub.toString());
        assertThat(c.getIssuer()).isEqualTo(s.getIssuer());
        assertThat(c.getAudience()).contains(s.getAudience());
        assertThat(c.getIssueTime().toInstant()).isEqualTo(now);
        assertThat(c.getExpirationTime().toInstant()).isEqualTo(now.plus(Duration.ofMinutes(15)));
        assertThat(c.getJWTID()).isNotBlank();

        // Roles & perm_ver
        assertThat(c.getClaim("roles")).isInstanceOf(List.class);
        assertThat((List<String>) c.getClaim("roles")).containsExactly("ADMIN","USER");
        Number pv = (Number) c.getClaim("perm_ver");
        assertThat(pv.intValue()).isEqualTo(3);
    }

    @Test
    void jwks_containsSingleRsaKey_withKid_andTtlSeconds() {
        JwtSettings s = new JwtSettings();
        s.setIssuer("iss");
        s.setAudience("aud");
        s.setAccessTtl("PT20M");

        JwtProviderImpl p = new JwtProviderImpl(s);

        // TTL
        assertThat(p.getAccessTtlSeconds()).isEqualTo(Duration.ofMinutes(20).toSeconds());

        // JWKS (Map)
        Map<String,Object> jwks = p.currentJwks();
        List<?> keys = (List<?>) jwks.get("keys");

        assertThat(keys).hasSize(1);

        @SuppressWarnings("unchecked")
        Map<String,Object> key0 = (Map<String,Object>) keys.get(0);

        assertThat(key0.get("kty")).isEqualTo("RSA");
        assertThat(key0.get("kid")).isEqualTo(p.getKid());
        assertThat(key0).containsKeys("n","e");
    }
}
