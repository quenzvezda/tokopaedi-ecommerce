package com.example.auth.infrastructure.persistence.repo;

import com.example.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByIdAndRevokedFalse(UUID id);
    long deleteByAccountIdAndExpiresAtBefore(UUID accountId, OffsetDateTime before);
}
