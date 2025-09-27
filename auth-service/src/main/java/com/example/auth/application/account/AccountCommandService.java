package com.example.auth.application.account;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.account.PasswordHasher;
import com.example.auth.web.error.EmailAlreadyExistsException;
import com.example.auth.web.error.UsernameAlreadyExistsException;
import com.example.common.messaging.AccountRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountCommandService implements AccountCommands {

    private static final Logger log = LoggerFactory.getLogger(AccountCommandService.class);

    private final AccountRepository accountRepository;
    private final PasswordHasher passwordHasher;
    private final AccountRegistrationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UUID register(String username, String email, String rawPassword, String fullName, String phone) {
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
        Account saved = accountRepository.save(a);

        AccountRegisteredEvent event = new AccountRegisteredEvent(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                fullName,
                (phone != null && !phone.isBlank()) ? phone : null
        );

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishEvent(event);
                }
            });
        } else {
            publishEvent(event);
        }

        return saved.getId();
    }

    private void publishEvent(AccountRegisteredEvent event) {
        try {
            eventPublisher.publish(event);
            log.info("Published account registered event for accountId={} username={}",
                    event.accountId(), event.username());
        } catch (Exception ex) {
            log.error("Failed to publish account registered event for accountId={}: {}",
                    event.accountId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
