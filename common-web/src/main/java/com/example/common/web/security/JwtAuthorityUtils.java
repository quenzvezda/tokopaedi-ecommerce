package com.example.common.web.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper untuk konversi authority dari JWT.
 * Default: baca claim "roles" → ROLE_*, optional gabung scope (SCOPE_*).
 */
public final class JwtAuthorityUtils {

    private JwtAuthorityUtils() {}

    /** Converter untuk klaim roles (list<string>) → ROLE_* */
    public static Converter<Jwt, Collection<GrantedAuthority>> rolesConverter(String claimName, String rolePrefix) {
        final String prefix = (rolePrefix == null || rolePrefix.isBlank()) ? "ROLE_" : rolePrefix;
        return (Jwt jwt) -> {
            Object claim = jwt.getClaim(claimName);
            if (claim instanceof Collection<?> col) {
                return col.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .map(r -> r.trim())
                        .filter(s -> !s.isEmpty())
                        .map(r -> new SimpleGrantedAuthority(prefix + r.toUpperCase(Locale.ROOT)))
                        .collect(Collectors.toUnmodifiableList());
            }
            return List.of();
        };
    }

    /** Converter bawaan Spring untuk scope/scp → SCOPE_* */
    public static Converter<Jwt, Collection<GrantedAuthority>> scopesConverter(String authorityPrefix, String claim) {
        JwtGrantedAuthoritiesConverter c = new JwtGrantedAuthoritiesConverter();
        if (authorityPrefix != null) c.setAuthorityPrefix(authorityPrefix);
        if (claim != null) c.setAuthoritiesClaimName(claim);
        return c;
    }

    /** JwtAuthenticationConverter hanya dari roles (klaim `roles`). */
    public static JwtAuthenticationConverter rolesOnly(String rolesClaim) {
        Converter<Jwt, Collection<GrantedAuthority>> conv = rolesConverter(defaultStr(rolesClaim, "roles"), "ROLE_");
        JwtAuthenticationConverter jac = new JwtAuthenticationConverter();
        jac.setJwtGrantedAuthoritiesConverter(conv);
        return jac;
    }

    /** JwtAuthenticationConverter gabungan: roles + scopes (opsional). */
    public static JwtAuthenticationConverter rolesAndScopes(String rolesClaim, boolean includeScopes) {
        Converter<Jwt, Collection<GrantedAuthority>> roles = rolesConverter(defaultStr(rolesClaim, "roles"), "ROLE_");
        Converter<Jwt, Collection<GrantedAuthority>> scopes = includeScopes
                ? scopesConverter("SCOPE_", null) // default klaim "scope"/"scp"
                : (jwt) -> List.of();

        JwtAuthenticationConverter jac = new JwtAuthenticationConverter();
        jac.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> res = new ArrayList<>();
            res.addAll(roles.convert(jwt));
            res.addAll(scopes.convert(jwt));
            return res;
        });
        return jac;
    }

    private static String defaultStr(String s, String d) { return (s == null || s.isBlank()) ? d : s; }
}
