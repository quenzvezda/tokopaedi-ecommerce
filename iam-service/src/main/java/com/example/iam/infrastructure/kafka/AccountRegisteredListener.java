package com.example.iam.infrastructure.kafka;

import com.example.common.messaging.AccountRegisteredEvent;
import com.example.iam.application.registration.AccountRegistrationHandler;
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
            topics = "${iam.registration.account-registered.topic:account-registered}",
            groupId = "${iam.registration.consumer-group:iam-service}",
            containerFactory = "accountRegisteredKafkaListenerContainerFactory"
    )
    public void onMessage(@Payload AccountRegisteredEvent event) {
        log.debug("Received account registered event for accountId={} username={}",
                event.accountId(), event.username());
        handler.handle(event);
    }
}
