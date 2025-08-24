package com.example.auth.infrastructure.jpa;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.infrastructure.jpa.mapper.JpaMapper;
import com.example.auth.infrastructure.jpa.repository.JpaAccountRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter JPA → Domain: AccountRepositoryImpl
 * Tidak memakai stereotype; di-wire lewat BeanConfig (Factory-only).
 */
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository repo;

    public AccountRepositoryImpl(JpaAccountRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return repo.findByUsername(username).map(JpaMapper::toDomain);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return repo.findByEmail(email).map(JpaMapper::toDomain);
    }

    @Override
    public Optional<Account> findByUsernameOrEmail(String usernameOrEmail) {
        return repo.findByUsernameIgnoreCaseOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
                .map(JpaMapper::toDomain);
    }

    @Override
    public Account save(Account a) {
        return JpaMapper.toDomain(repo.save(JpaMapper.toEntity(a)));
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return repo.findById(id).map(JpaMapper::toDomain);
    }
}
