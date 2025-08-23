package com.example.catalog.infrastructure.iam;

import com.example.catalog.application.authz.EntitlementsQuery;
import com.example.catalog.security.EntitlementsDto;
import com.example.catalog.security.IamClientProperties;
import com.example.catalog.security.IamEntitlementsClient;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementasi Query dengan L1 cache (Caffeine).
 * TODO: Tambahkan L2 cache (Redis) untuk berbagi cache antar instance service.
 */
@RequiredArgsConstructor
public class CachedEntitlementsQuery implements EntitlementsQuery {

    private final Cache<UUID, Object> cache; // simpan CachedEntry sebagai Object agar bean cache generik sederhana
    private final IamEntitlementsClient client;
    private final IamClientProperties props;

    @Override
    public Collection<GrantedAuthority> findAuthorities(UUID accountId, Integer tokenPermVer) {
        CachedEntry cached = (CachedEntry) cache.getIfPresent(accountId);
        if (cached != null && tokenPermVer != null && cached.permVer == tokenPermVer) {
            return cached.authorities;
        }
        EntitlementsDto dto = client.fetch(accountId);
        int pv = dto.perm_ver() != null ? dto.perm_ver() : (tokenPermVer != null ? tokenPermVer : -1);
        Collection<GrantedAuthority> mapped = mapScopes(dto.scopes(), props.getScopePrefix());
        cache.put(accountId, new CachedEntry(pv, mapped));
        return mapped;
    }

    private static Collection<GrantedAuthority> mapScopes(List<String> scopes, String prefix) {
        if (scopes == null || scopes.isEmpty()) return List.of();
        String pfx = (prefix == null) ? "" : prefix;
        return scopes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> new SimpleGrantedAuthority(pfx + s))
                .collect(Collectors.toUnmodifiableList());
    }

    static final class CachedEntry {
        final int permVer;
        final Collection<GrantedAuthority> authorities;
        CachedEntry(int permVer, Collection<GrantedAuthority> authorities) {
            this.permVer = permVer;
            this.authorities = authorities;
        }
    }
}
