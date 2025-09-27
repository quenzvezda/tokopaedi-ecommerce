package com.example.profile.infrastructure.jpa.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaStoreProfileRepository extends JpaRepository<StoreProfileEntity, UUID> {
    List<StoreProfileEntity> findByOwnerId(UUID ownerId);

    Optional<StoreProfileEntity> findByIdAndOwnerId(UUID id, UUID ownerId);

    boolean existsByOwnerId(UUID ownerId);
}
