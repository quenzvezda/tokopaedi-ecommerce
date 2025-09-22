package com.example.auth.infrastructure.jpa;

import com.example.auth.domain.account.Account;
import com.example.auth.infrastructure.jpa.repository.JpaAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void search_filtersByUsername() {
        persistAccount(UUID.fromString("00000000-0000-0000-0000-000000000001"), "alice");
        persistAccount(UUID.fromString("00000000-0000-0000-0000-000000000002"), "bob");

        var result = adapter.search("Al", List.of("username"), 0, 10);

        assertThat(result.content()).extracting(Account::getUsername).containsExactly("alice");
    }

    @Test
    void search_sanitizesPaginationAndDefaultsSort() {
        persistAccount(UUID.fromString("00000000-0000-0000-0000-000000000003"), "alice");
        persistAccount(UUID.fromString("00000000-0000-0000-0000-000000000004"), "charlie");

        var result = adapter.search(null, null, -5, 0);

        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(2);
        assertThat(result.content()).extracting(Account::getUsername).containsExactly("alice");
    }

    @Test
    void search_supportsInlineDirectionSort() {
        var first = persistAccount(UUID.fromString("00000000-0000-0000-0000-000000000010"), "alice");
        var second = persistAccount(UUID.fromString("00000000-0000-0000-0000-000000000020"), "bob");

        var result = adapter.search(null, List.of("id,desc"), 0, 10);

        assertThat(result.content()).extracting(Account::getId).containsExactly(second.getId(), first.getId());
    }

    @Test
    void search_supportsDirectionTokenAndSkipsBlankEntries() {
        persistAccount(UUID.fromString("00000000-0000-0000-0000-000000000100"), "alice");
        persistAccount(UUID.fromString("00000000-0000-0000-0000-000000000200"), "bob");
        persistAccount(UUID.fromString("00000000-0000-0000-0000-000000000300"), "charlie");

        var result = adapter.search(null, List.of(" ", "username", "DESC"), 0, 10);

        assertThat(result.content()).extracting(Account::getUsername).containsExactly("charlie", "bob", "alice");
    }

    @Test
    void search_rejectsUnknownSortField() {
        assertThatThrownBy(() -> adapter.search(null, List.of("email"), 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid sort field");
    }

    @Test
    void search_rejectsUnknownSortDirection() {
        assertThatThrownBy(() -> adapter.search(null, List.of("id,down"), 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid sort direction");
    }

    private Account persistAccount(UUID id, String username) {
        Account account = Account.of(id, username, username + "@example.com", "HASH", "ACTIVE", OffsetDateTime.now());
        return adapter.save(account);
    }
}
