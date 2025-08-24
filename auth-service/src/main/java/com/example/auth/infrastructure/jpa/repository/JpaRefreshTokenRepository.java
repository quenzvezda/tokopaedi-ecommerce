package com.example.auth.infrastructure.jpa.repository;

import com.example.auth.infrastructure.jpa.entity.JpaRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface JpaRefreshTokenRepository extends JpaRepository<JpaRefreshToken, UUID> {
    Optional<JpaRefreshToken> findByIdAndRevokedFalse(UUID id);
    long deleteByAccountIdAndExpiresAtBefore(UUID accountId, OffsetDateTime before);
}
