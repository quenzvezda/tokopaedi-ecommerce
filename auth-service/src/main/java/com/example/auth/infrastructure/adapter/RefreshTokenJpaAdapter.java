package com.example.auth.infrastructure.adapter;

import com.example.auth.domain.model.RefreshToken;
import com.example.auth.domain.port.RefreshTokenPort;
import com.example.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import com.example.auth.infrastructure.persistence.repo.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class RefreshTokenJpaAdapter implements RefreshTokenPort {
    private final RefreshTokenJpaRepository repo;

    @Override
    public RefreshToken create(UUID id, UUID accountId, Instant now) {
        OffsetDateTime exp = OffsetDateTime.ofInstant(now.plusSeconds(7 * 24 * 3600L), ZoneOffset.UTC);
        RefreshTokenEntity e = new RefreshTokenEntity();
        e.setId(id);
        e.setAccountId(accountId);
        e.setExpiresAt(exp);
        e.setRevoked(false);
        return toDomain(repo.save(e));
    }

    @Override public Optional<RefreshToken> findById(UUID id) { return repo.findById(id).map(this::toDomain); }

    @Override public void consume(UUID id) {
        repo.findById(id).ifPresent(e -> { e.setRevoked(true); repo.save(e); });
    }

    private RefreshToken toDomain(RefreshTokenEntity e) {
        return RefreshToken.of(e.getId(), e.getAccountId(), e.getExpiresAt(), e.isRevoked());
    }
}
