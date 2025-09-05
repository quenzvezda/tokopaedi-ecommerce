package com.example.auth.infrastructure.jpa;

import com.example.auth.domain.token.RefreshToken;
import com.example.auth.domain.token.RefreshTokenRepository;
import com.example.auth.infrastructure.jpa.entity.JpaRefreshToken;
import com.example.auth.infrastructure.jpa.mapper.JpaMapper;
import com.example.auth.infrastructure.jpa.repository.JpaRefreshTokenRepository;

import java.time.*;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter JPA → Domain: RefreshTokenRepositoryImpl
 * TTL refresh token hard-coded 7 hari. (TODO: Ganti / pindah ttl config ke .yml)
 */
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository repo;
    private final Duration refreshTtl;

    public RefreshTokenRepositoryImpl(JpaRefreshTokenRepository repo, Duration refreshTtl) {
        this.repo = repo;
        this.refreshTtl = refreshTtl != null ? refreshTtl : Duration.ofDays(7);
    }

    @Override
    public RefreshToken create(UUID id, UUID accountId, Instant now) {
        OffsetDateTime exp = OffsetDateTime.ofInstant(now.plus(refreshTtl), ZoneOffset.UTC);
        JpaRefreshToken e = new JpaRefreshToken();
        e.setId(id);
        e.setAccountId(accountId);
        e.setExpiresAt(exp);
        e.setRevoked(false);
        return JpaMapper.toDomain(repo.save(e));
    }

    @Override
    public Optional<RefreshToken> findById(UUID id) {
        return repo.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public void consume(UUID id) {
        repo.findById(id).ifPresent(e -> {
            e.setRevoked(true);
            repo.save(e);
        });
    }
}
