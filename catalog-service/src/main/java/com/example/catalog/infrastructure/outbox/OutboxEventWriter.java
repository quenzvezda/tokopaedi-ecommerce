package com.example.catalog.infrastructure.outbox;

import com.example.catalog.application.sku.events.SkuActivated;
import com.example.catalog.application.sku.events.SkuCreated;
import com.example.catalog.application.sku.events.SkuDeactivated;
import com.example.catalog.infrastructure.jpa.entity.OutboxEventEntity;
import com.example.catalog.infrastructure.jpa.repository.JpaOutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventWriter {

    private static final String TOPIC = "catalog-events"; // informational only; publisher uses this

    private final JpaOutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onSkuCreated(SkuCreated e) {
        {
            var map = new java.util.LinkedHashMap<String, Object>();
            map.put("eventId", UUID.randomUUID());
            map.put("eventType", "catalog.sku.created");
            map.put("skuId", e.skuId());
            map.put("productId", e.productId());
            writeEvent("catalog", e.skuId(), "catalog.sku.created", map);
        }
        if (e.active()) {
            var map2 = new java.util.LinkedHashMap<String, Object>();
            map2.put("eventId", UUID.randomUUID());
            map2.put("eventType", "catalog.sku.activated");
            map2.put("skuId", e.skuId());
            map2.put("productId", null);
            writeEvent("catalog", e.skuId(), "catalog.sku.activated", map2);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onSkuActivated(SkuActivated e) {
        var map = new java.util.LinkedHashMap<String, Object>();
        map.put("eventId", UUID.randomUUID());
        map.put("eventType", "catalog.sku.activated");
        map.put("skuId", e.skuId());
        map.put("productId", null);
        writeEvent("catalog", e.skuId(), "catalog.sku.activated", map);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onSkuDeactivated(SkuDeactivated e) {
        var map = new java.util.LinkedHashMap<String, Object>();
        map.put("eventId", UUID.randomUUID());
        map.put("eventType", "catalog.sku.deactivated");
        map.put("skuId", e.skuId());
        map.put("productId", null);
        writeEvent("catalog", e.skuId(), "catalog.sku.deactivated", map);
    }

    private void writeEvent(String aggregateType, UUID aggregateId, String eventType, Map<String, Object> payload) {
        try {
            String key = aggregateId != null ? aggregateId.toString() : UUID.randomUUID().toString();
            String json = objectMapper.writeValueAsString(payload);
            var entity = OutboxEventEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .eventKey(key)
                    .payload(json)
                    .createdAt(Instant.now())
                    .attempts(0)
                    .build();
            outboxRepo.save(entity);
        } catch (Exception ex) {
            log.error("Failed to write outbox for {} id={}", eventType, aggregateId, ex);
            throw new RuntimeException(ex);
        }
    }
}
