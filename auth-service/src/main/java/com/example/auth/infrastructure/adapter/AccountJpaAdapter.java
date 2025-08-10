package com.example.auth.infrastructure.adapter;

import com.example.auth.domain.model.Account;
import com.example.auth.domain.port.AccountPort;
import com.example.auth.infrastructure.persistence.entity.AccountEntity;
import com.example.auth.infrastructure.persistence.repo.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountJpaAdapter implements AccountPort {
    private final AccountJpaRepository repo;

    @Override
    public Optional<Account> findByUsername(String username) { return repo.findByUsername(username).map(this::toDomain); }

    @Override
    public Optional<Account> findByEmail(String email) { return repo.findByEmail(email).map(this::toDomain); }

    @Override
    public Account save(Account a) {
        AccountEntity e = new AccountEntity();
        e.setId(a.getId()); e.setUsername(a.getUsername()); e.setEmail(a.getEmail()); e.setPasswordHash(a.getPasswordHash()); e.setStatus(a.getStatus()); e.setCreatedAt(a.getCreatedAt());
        return toDomain(repo.save(e));
    }

    @Override
    public Optional<Account> findById(UUID id) { return repo.findById(id).map(this::toDomain); }

    private Account toDomain(AccountEntity e) { return Account.of(e.getId(), e.getUsername(), e.getEmail(), e.getPasswordHash(), e.getStatus(), e.getCreatedAt()); }
}
