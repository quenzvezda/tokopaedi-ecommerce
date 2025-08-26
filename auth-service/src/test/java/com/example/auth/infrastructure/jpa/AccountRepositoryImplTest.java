package com.example.auth.infrastructure.jpa;

import com.example.auth.domain.account.Account;
import com.example.auth.infrastructure.jpa.repository.JpaAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryImplTest {

    @Autowired
    JpaAccountRepository jpaRepo;

    AccountRepositoryImpl adapter;

    @BeforeEach
    void setUp() {
        adapter = new AccountRepositoryImpl(jpaRepo);
    }

    @Test
    void save_and_findById_username_email_defaultsApplied() {
        UUID id = UUID.randomUUID();
        // status & createdAt sengaja null → harus terisi oleh @PrePersist pada entity
        Account acc = Account.of(id, "alice", "a@x.io", "HASH", null, null);

        Account saved = adapter.save(acc);
        assertThat(saved.getId()).isEqualTo(id);

        var byId = adapter.findById(id).orElseThrow();
        assertThat(byId.getUsername()).isEqualTo("alice");
        assertThat(byId.getEmail()).isEqualTo("a@x.io");
        assertThat(byId.getStatus()).isEqualTo("ACTIVE");
        assertThat(byId.getCreatedAt()).isNotNull();

        assertThat(adapter.findByUsername("alice")).isPresent();
        assertThat(adapter.findByEmail("a@x.io")).isPresent();
    }

    @Test
    void findByUsernameOrEmail_caseInsensitive() {
        UUID id = UUID.randomUUID();
        Account acc = Account.of(id, "bob", "b@example.com", "H", "ACTIVE", OffsetDateTime.now());
        adapter.save(acc);

        assertThat(adapter.findByUsernameOrEmail("BOB")).isPresent();              // username ignore-case
        assertThat(adapter.findByUsernameOrEmail("B@EXAMPLE.COM")).isPresent();    // email ignore-case
        assertThat(adapter.findByUsernameOrEmail("nope")).isEmpty();
    }
}
