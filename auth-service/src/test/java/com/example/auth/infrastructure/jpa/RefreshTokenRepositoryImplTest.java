package com.example.auth.infrastructure.jpa;

import com.example.auth.domain.token.RefreshToken;
import com.example.auth.infrastructure.jpa.entity.JpaRefreshToken;
import com.example.auth.infrastructure.jpa.repository.JpaRefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RefreshTokenRepositoryImplTest {

    @Autowired
    JpaRefreshTokenRepository jpaRepo;

    RefreshTokenRepositoryImpl adapter;

    @BeforeEach
    void setUp() {
        adapter = new RefreshTokenRepositoryImpl(jpaRepo);
    }

    @Test
    void create_persistsWithSevenDaysTtl_andReturnsDomain() {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        RefreshToken saved = adapter.create(id, accountId, now);

        // assert domain
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getAccountId()).isEqualTo(accountId);
        assertThat(saved.isRevoked()).isFalse();

        OffsetDateTime expectedExp = OffsetDateTime.ofInstant(
                now.plusSeconds(7L * 24 * 3600), ZoneOffset.UTC);
        assertThat(saved.getExpiresAt()).isEqualTo(expectedExp);

        // assert row DB
        JpaRefreshToken row = jpaRepo.findById(id).orElseThrow();
        assertThat(row.getAccountId()).isEqualTo(accountId);
        assertThat(row.isRevoked()).isFalse();
        assertThat(row.getExpiresAt()).isEqualTo(expectedExp);
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        assertThat(adapter.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void consume_marksRevokedTrue_andNotVisibleInFindByIdAndRevokedFalse() {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        UUID id = UUID.randomUUID();
        UUID acc = UUID.randomUUID();

        adapter.create(id, acc, now);
        adapter.consume(id);

        JpaRefreshToken row = jpaRepo.findById(id).orElseThrow();
        assertThat(row.isRevoked()).isTrue();
        assertThat(jpaRepo.findByIdAndRevokedFalse(id)).isEmpty();
    }
}
