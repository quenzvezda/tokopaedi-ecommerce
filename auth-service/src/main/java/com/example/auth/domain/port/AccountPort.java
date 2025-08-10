package com.example.auth.domain.port;

import com.example.auth.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountPort {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    Account save(Account a);
    Optional<Account> findById(UUID id);
}
