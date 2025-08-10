package com.example.auth.domain.port;

import com.example.auth.domain.model.Entitlements;

import java.util.Optional;
import java.util.UUID;

public interface EntitlementsStorePort {
    Optional<Entitlements> get(UUID accountId);
    Entitlements upsertIfNewer(Entitlements incoming);
}
