package com.example.auth.infrastructure.kafka;

import com.example.auth.domain.model.Entitlements;
import com.example.auth.domain.port.EntitlementsStorePort;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IamPermissionsListener {
    private final EntitlementsStorePort store;
    @Value("${topics.entitlements}") private String topic;

    @KafkaListener(topics = "${topics.entitlements}", containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, IamPermissionEvent> rec) {
        IamPermissionEvent ev = rec.value();
        UUID accountId = parseAccountId(rec.key(), ev.getAccount_id());
        if (accountId == null || ev.getPerm_ver() == null || ev.getRoles() == null) return;
        Entitlements incoming = Entitlements.of(accountId, ev.getPerm_ver(), ev.getRoles(), ev.eventTime() == null ? Instant.now() : ev.eventTime());
        store.upsertIfNewer(incoming);
    }

    private UUID parseAccountId(String key, String bodyField) {
        try { return key != null ? UUID.fromString(key) : UUID.fromString(bodyField); } catch (Exception e) { return null; }
    }
}
