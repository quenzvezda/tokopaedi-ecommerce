package com.example.auth.domain.account;

import com.example.auth.domain.common.PageResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Optional<Account> findById(UUID id);
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUsernameOrEmail(String usernameOrEmail);
    Account save(Account account);
    PageResult<Account> search(String q, List<String> sort, int page, int size);
}
