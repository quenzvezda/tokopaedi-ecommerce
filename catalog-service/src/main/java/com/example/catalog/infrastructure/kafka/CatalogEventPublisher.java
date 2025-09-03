package com.example.catalog.infrastructure.kafka;

import com.example.catalog.application.sku.events.SkuActivated;
import com.example.catalog.application.sku.events.SkuCreated;
import com.example.catalog.application.sku.events.SkuDeactivated;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogEventPublisher {

    private static final String TOPIC = "catalog-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSkuCreated(SkuCreated e) {
        send("catalog.sku.created", e.skuId(), e.productId());
        if (e.active()) {
            send("catalog.sku.activated", e.skuId(), null);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSkuActivated(SkuActivated e) {
        send("catalog.sku.activated", e.skuId(), null);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSkuDeactivated(SkuDeactivated e) {
        send("catalog.sku.deactivated", e.skuId(), null);
    }

    private void send(String eventType, UUID skuId, UUID productId) {
        try {
            var payload = new CatalogEvent(UUID.randomUUID(), eventType, skuId, productId);
            String key = skuId != null ? skuId.toString() : UUID.randomUUID().toString();
            String message = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(TOPIC, key, message);
            log.info("Published {} for skuId={} productId={}", eventType, skuId, productId);
        } catch (Exception ex) {
            log.error("Failed to publish {} for skuId={}", eventType, skuId, ex);
        }
    }

    private record CatalogEvent(UUID eventId, String eventType, UUID skuId, UUID productId) {}
}

