package com.example.catalog.infrastructure.iam;

import com.example.catalog.security.EntitlementsDto;
import com.example.catalog.security.IamClientProperties;
import com.example.catalog.security.IamEntitlementsClient;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CachedEntitlementsQueryTest {

    IamEntitlementsClient client = mock(IamEntitlementsClient.class);
    IamClientProperties props = new IamClientProperties();
    CachedEntitlementsQuery query = new CachedEntitlementsQuery(Caffeine.newBuilder().build(), client, props);

    @Test
    void fetchesAndCaches() {
        UUID id = UUID.randomUUID();
        // use raw authorities (no prefix)
        props.setScopePrefix("");
        when(client.fetch(id)).thenReturn(new EntitlementsDto(1, List.of("a")));

        Collection<GrantedAuthority> first = query.findAuthorities(id, 1);
        assertThat(first).extracting(GrantedAuthority::getAuthority).contains("a");
        verify(client, times(1)).fetch(id);

        Collection<GrantedAuthority> second = query.findAuthorities(id, 1);
        assertThat(second).hasSize(1);
        verify(client, times(1)).fetch(id); // cached

        query.findAuthorities(id, 2); // permVer changed -> re-fetch
        verify(client, times(2)).fetch(id);
    }
}

