package com.example.auth.application.account;

import com.example.common.messaging.AccountRegisteredEvent;

/**
 * Port untuk menerbitkan event ketika akun baru berhasil dibuat.
 */
public interface AccountRegistrationEventPublisher {

    void publish(AccountRegisteredEvent event);
}
