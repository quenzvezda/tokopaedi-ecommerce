package com.example.auth.infrastructure.jpa;

import com.example.auth.infrastructure.jpa.entity.JpaRefreshToken;
import com.example.auth.infrastructure.jpa.repository.JpaRefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaRefreshTokenRepositoryTest {

    @Autowired
    JpaRefreshTokenRepository repo;

    @Test
    void saveAndFind() {
        var e = new JpaRefreshToken();
        e.setId(UUID.randomUUID());
        e.setAccountId(UUID.randomUUID());
        e.setExpiresAt(OffsetDateTime.now().plusDays(7));
        e.setRevoked(false);

        repo.saveAndFlush(e);

        var found = repo.findById(e.getId());
        assertThat(found).isPresent();
        assertThat(found.get().isRevoked()).isFalse();
    }

    @Test
    void prePersist_generatesIdWhenNull() {
        var e = new JpaRefreshToken();
        e.setAccountId(UUID.randomUUID());
        e.setExpiresAt(OffsetDateTime.now().plusDays(7));
        e.setRevoked(false);

        var saved = repo.saveAndFlush(e);
        assertThat(saved.getId()).isNotNull(); // diisi @PrePersist
    }

    @Test
    void findByIdAndRevokedFalse_filtersRevoked() {
        var a = UUID.randomUUID();

        var active = new JpaRefreshToken();
        active.setAccountId(a);
        active.setExpiresAt(OffsetDateTime.now().plusDays(7));
        active.setRevoked(false);
        active = repo.saveAndFlush(active);

        var revoked = new JpaRefreshToken();
        revoked.setAccountId(a);
        revoked.setExpiresAt(OffsetDateTime.now().plusDays(7));
        revoked.setRevoked(true);
        revoked = repo.saveAndFlush(revoked);

        assertThat(repo.findByIdAndRevokedFalse(active.getId())).isPresent();
        assertThat(repo.findByIdAndRevokedFalse(revoked.getId())).isEmpty();
    }

    @Test
    void deleteByAccountIdAndExpiresAtBefore_deletesOnlyExpiredForThatAccount() {
        UUID account = UUID.randomUUID();

        var expired = new JpaRefreshToken();
        expired.setAccountId(account);
        expired.setExpiresAt(OffsetDateTime.now().minusDays(1)); // expired
        expired.setRevoked(false);
        expired = repo.saveAndFlush(expired);

        var future = new JpaRefreshToken();
        future.setAccountId(account);
        future.setExpiresAt(OffsetDateTime.now().plusDays(1)); // masih aktif
        future.setRevoked(false);
        future = repo.saveAndFlush(future);

        var otherAccountExpired = new JpaRefreshToken();
        otherAccountExpired.setAccountId(UUID.randomUUID());
        otherAccountExpired.setExpiresAt(OffsetDateTime.now().minusDays(2));
        otherAccountExpired.setRevoked(false);
        otherAccountExpired = repo.saveAndFlush(otherAccountExpired);

        long deleted = repo.deleteByAccountIdAndExpiresAtBefore(account, OffsetDateTime.now());
        assertThat(deleted).isEqualTo(1);

        assertThat(repo.findById(expired.getId())).isEmpty();        // terhapus
        assertThat(repo.findById(future.getId())).isPresent();       // tetap ada
        assertThat(repo.findById(otherAccountExpired.getId())).isPresent(); // bukan target
    }
}
