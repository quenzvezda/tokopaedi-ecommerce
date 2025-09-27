package com.example.profile.domain.store;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreProfileRepository {
    StoreProfile save(StoreProfile storeProfile);

    List<StoreProfile> findByOwnerId(UUID ownerId);

    Optional<StoreProfile> findByIdAndOwnerId(UUID id, UUID ownerId);

    boolean existsByOwnerId(UUID ownerId);
}
