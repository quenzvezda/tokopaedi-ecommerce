package com.example.profile.application.registration;

import com.example.common.messaging.AccountRegisteredEvent;
import com.example.profile.application.profile.ProfileCommands;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class AccountRegistrationHandler {

    private static final Logger log = LoggerFactory.getLogger(AccountRegistrationHandler.class);

    private final ProfileCommands profileCommands;

    public void handle(AccountRegisteredEvent event) {
        var command = new ProfileCommands.CreateInitialProfileCommand(event.fullName(), normalize(event.phone()));
        profileCommands.createInitialProfile(event.accountId(), command);
        log.info("Created default profile for account {}", event.accountId());
    }

    private String normalize(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return phone;
    }
}
