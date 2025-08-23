package com.example.catalog.security;

import com.example.catalog.application.authz.EntitlementsQuery;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * Filter untuk meng-augment authorities JWT (ROLE_*) dengan entitlements (scopes → SCOPE_*).
 */
@Component
@RequiredArgsConstructor
public class ScopeAuthorityAugmentorFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ScopeAuthorityAugmentorFilter.class);

    private final EntitlementsQuery entitlementsQuery;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String sub = jwt.getSubject();
            Integer permVer = null;
            Object pv = jwt.getClaim("perm_ver");
            if (pv instanceof Number n) permVer = n.intValue();

            try {
                UUID accountId = UUID.fromString(sub);
                Collection<GrantedAuthority> current = new ArrayList<>(jwtAuth.getAuthorities());
                Set<String> names = new HashSet<>();
                current.forEach(a -> names.add(a.getAuthority()));

                Collection<GrantedAuthority> scopes = entitlementsQuery.findAuthorities(accountId, permVer);
                for (GrantedAuthority ga : scopes) {
                    if (names.add(ga.getAuthority())) current.add(ga);
                }

                JwtAuthenticationToken augmented = new JwtAuthenticationToken(jwt, current, jwt.getSubject());
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(augmented);
            } catch (IllegalArgumentException e) {
                log.debug("JWT sub is not a UUID → skip entitlements fetch");
            } catch (Exception e) {
                log.warn("Entitlements fetch/merge failed: {}", e.toString());
            }
        }
        chain.doFilter(req, res);
    }
}
