package com.example.profile.infrastructure.jpa.store;

import com.example.profile.domain.store.StoreProfile;
import com.example.profile.domain.store.StoreProfileRepository;
import com.example.profile.domain.store.StoreSlugAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class StoreProfileRepositoryImpl implements StoreProfileRepository {

    private static final String OWNER_SLUG_CONSTRAINT = "store_profiles_owner_slug_idx";

    private final JpaStoreProfileRepository jpaRepository;

    @Override
    @Transactional
    public StoreProfile save(StoreProfile storeProfile) {
        StoreProfileEntity entity = StoreProfileMapper.toEntity(storeProfile);
        try {
            StoreProfileEntity saved = jpaRepository.save(entity);
            return StoreProfileMapper.toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            if (isOwnerSlugConflict(ex)) {
                throw new StoreSlugAlreadyExistsException(storeProfile.getOwnerId(), storeProfile.getSlug(), ex);
            }
            throw ex;
        }
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

    private boolean isOwnerSlugConflict(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof ConstraintViolationException cve) {
            String constraint = cve.getConstraintName();
            return constraint != null && constraint.equalsIgnoreCase(OWNER_SLUG_CONSTRAINT);
        }
        return false;
    }
}
