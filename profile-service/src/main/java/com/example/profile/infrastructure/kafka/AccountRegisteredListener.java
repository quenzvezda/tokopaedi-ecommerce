package com.example.profile.infrastructure.kafka;

import com.example.common.messaging.AccountRegisteredEvent;
import com.example.profile.application.registration.AccountRegistrationHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountRegisteredListener {

    private static final Logger log = LoggerFactory.getLogger(AccountRegisteredListener.class);

    private final AccountRegistrationHandler handler;

    @KafkaListener(
            topics = "${profile.kafka.account-registered.topic:account-registered}",
            groupId = "${profile.kafka.consumer-group:profile-service}",
            containerFactory = "accountRegisteredKafkaListenerContainerFactory"
    )
    public void consume(@Payload AccountRegisteredEvent event) {
        log.debug("Received account registration event for account {}", event.accountId());
        handler.handle(event);
    }
}
