package com.example.auth.application.account;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.account.PasswordHasher;
import com.example.auth.web.error.EmailAlreadyExistsException;
import com.example.auth.web.error.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountCommandService implements AccountCommands {

    private final AccountRepository accountRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public UUID register(String username, String email, String rawPassword) {
        accountRepository.findByUsername(username)
                .ifPresent(a -> { throw new UsernameAlreadyExistsException(); });
        accountRepository.findByEmail(email)
                .ifPresent(a -> { throw new EmailAlreadyExistsException(); });

        Account a = Account.of(
                UUID.randomUUID(),
                username,
                email,
                passwordHasher.encode(rawPassword),
                "ACTIVE",
                OffsetDateTime.now(ZoneOffset.UTC)
        );
        return accountRepository.save(a).getId();
    }
}
