package com.example.auth.application.command;

import com.example.auth.domain.model.Account;
import com.example.auth.domain.port.AccountPort;
import com.example.auth.domain.port.PasswordHasherPort;
import com.example.auth.web.dto.RegisterRequest;
import com.example.auth.web.error.EmailAlreadyExistsException;
import com.example.auth.web.error.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterCommand {
    private final AccountPort accountPort;
    private final PasswordHasherPort passwordHasher;

    public UUID handle(RegisterRequest req) {
        accountPort.findByUsername(req.getUsername()).ifPresent(a -> { throw new UsernameAlreadyExistsException(); });
        accountPort.findByEmail(req.getEmail()).ifPresent(a -> { throw new EmailAlreadyExistsException(); });

        Account a = Account.of(
                UUID.randomUUID(),
                req.getUsername(),
                req.getEmail(),
                passwordHasher.encode(req.getPassword()),
                "ACTIVE",
                OffsetDateTime.now(ZoneOffset.UTC)
        );
        Account saved = accountPort.save(a);
        return saved.getId();
    }
}
