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

		repo.save(e);
		var found = repo.findById(e.getId());
		assertThat(found).isPresent();
		assertThat(found.get().isRevoked()).isFalse();
	}
}
