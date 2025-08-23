package com.example.catalog.application.authz;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

/**
 * CQRS - Query kontrak untuk mengambil authorities (scopes) user.
 */
public interface EntitlementsQuery {
    /**
     * @param accountId     sub dari JWT (UUID user)
     * @param tokenPermVer  perm_ver dari JWT (untuk invalidasi cache)
     * @return authorities yang dipetakan dari entitlements (scopes).
     */
    Collection<GrantedAuthority> findAuthorities(UUID accountId, Integer tokenPermVer);
}
