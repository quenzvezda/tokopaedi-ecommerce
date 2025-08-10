package com.example.auth.infrastructure.adapter;

import com.example.auth.domain.model.Entitlements;
import com.example.auth.domain.port.EntitlementsStorePort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryEntitlementsStore implements EntitlementsStorePort {
    private final ConcurrentHashMap<UUID, Entitlements> map = new ConcurrentHashMap<>();

    @Override
    public Optional<Entitlements> get(UUID accountId) { return Optional.ofNullable(map.get(accountId)); }

    @Override
    public Entitlements upsertIfNewer(Entitlements incoming) {
        map.compute(incoming.getAccountId(), (k, curr) -> curr == null || incoming.getPermVer() > curr.getPermVer() ? incoming : curr);
        return map.get(incoming.getAccountId());
    }
}
