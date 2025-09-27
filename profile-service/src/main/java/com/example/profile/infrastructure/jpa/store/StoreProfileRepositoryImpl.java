package com.example.profile.infrastructure.jpa.store;

import com.example.profile.domain.store.StoreProfile;
import com.example.profile.domain.store.StoreProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class StoreProfileRepositoryImpl implements StoreProfileRepository {

    private final JpaStoreProfileRepository jpaRepository;

    @Override
    @Transactional
    public StoreProfile save(StoreProfile storeProfile) {
        StoreProfileEntity entity = StoreProfileMapper.toEntity(storeProfile);
        StoreProfileEntity saved = jpaRepository.save(entity);
        return StoreProfileMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreProfile> findByOwnerId(UUID ownerId) {
        return jpaRepository.findByOwnerId(ownerId).stream()
                .map(StoreProfileMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StoreProfile> findByIdAndOwnerId(UUID id, UUID ownerId) {
        return jpaRepository.findByIdAndOwnerId(id, ownerId).map(StoreProfileMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByOwnerId(UUID ownerId) {
        return jpaRepository.existsByOwnerId(ownerId);
    }
}
