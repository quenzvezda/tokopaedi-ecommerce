package com.example.auth.infrastructure.jpa;

import com.example.auth.infrastructure.jpa.entity.JpaAccount;
import com.example.auth.infrastructure.jpa.repository.JpaAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class JpaAccountRepositoryTest {

	@Autowired
	JpaAccountRepository repo;

	@Test
	void findByUsername_and_findByEmail() {
		var e = new JpaAccount();
		e.setId(UUID.randomUUID());
		e.setUsername("alice");
		e.setEmail("a@x.io");
		e.setPasswordHash("HASH");
		e.setStatus("ACTIVE");
		e.setCreatedAt(OffsetDateTime.now());

		repo.saveAndFlush(e);

		Optional<JpaAccount> byU = repo.findByUsername("alice");
		Optional<JpaAccount> byE = repo.findByEmail("a@x.io");
		assertThat(byU).isPresent();
		assertThat(byE).isPresent();
	}

	@Test
	void uniqueUsername_violation() {
		var a1 = new JpaAccount();
		a1.setId(UUID.randomUUID());
		a1.setUsername("unique");
		a1.setEmail("u1@x.io");
		a1.setPasswordHash("H");
		a1.setStatus("ACTIVE");
		a1.setCreatedAt(OffsetDateTime.now());
		repo.saveAndFlush(a1);

		var a2 = new JpaAccount();
		a2.setId(UUID.randomUUID());
		a2.setUsername("unique");      // same username
		a2.setEmail("u2@x.io");
		a2.setPasswordHash("H");
		a2.setStatus("ACTIVE");
		a2.setCreatedAt(OffsetDateTime.now());

		assertThatThrownBy(() -> { repo.saveAndFlush(a2); })
				.isInstanceOf(DataIntegrityViolationException.class);
	}
}
