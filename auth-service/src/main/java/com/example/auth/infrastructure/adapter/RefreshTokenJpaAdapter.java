package com.example.auth.infrastructure.adapter;

import com.example.auth.domain.model.RefreshToken;
import com.example.auth.domain.port.RefreshTokenPort;
import com.example.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import com.example.auth.infrastructure.persistence.repo.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenJpaAdapter implements RefreshTokenPort {
    private final RefreshTokenJpaRepository repo;

    @Override
    @Transactional
    public RefreshToken issue(UUID accountId, OffsetDateTime expiresAt) {
        RefreshTokenEntity e = new RefreshTokenEntity();
        e.setAccountId(accountId);
        e.setExpiresAt(expiresAt);
        e.setRevoked(false);
        RefreshTokenEntity s = repo.save(e);
        return toDomain(s);
    }

    @Override
    public Optional<RefreshToken> findActive(UUID id) { return repo.findByIdAndRevokedFalse(id).map(this::toDomain); }

    @Override
    @Transactional
    public void revoke(UUID id) { repo.findById(id).ifPresent(rt -> { rt.setRevoked(true); repo.save(rt); }); }

    @Override
    public long deleteExpired(UUID accountId, OffsetDateTime before) { return repo.deleteByAccountIdAndExpiresAtBefore(accountId, before); }

    private RefreshToken toDomain(RefreshTokenEntity e) { return RefreshToken.of(e.getId(), e.getAccountId(), e.getExpiresAt(), e.isRevoked()); }
}
