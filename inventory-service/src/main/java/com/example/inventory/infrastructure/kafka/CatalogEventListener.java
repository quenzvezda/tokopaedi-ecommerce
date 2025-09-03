package com.example.inventory.infrastructure.kafka;

import com.example.inventory.application.stock.StockCommands;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogEventListener {

    private final StockCommands stockCommands;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "catalog-events", groupId = "inventory-service")
    public void handle(String message) {
        CatalogEvent evt;
        try {
            evt = objectMapper.readValue(message, CatalogEvent.class);
        } catch (Exception e) {
            // Let the container error handler route to DLT
            throw new IllegalArgumentException("Invalid catalog event payload", e);
        }

        UUID eventId = evt.eventId();
        switch (evt.eventType()) {
            case "catalog.sku.created" ->
                    stockCommands.handleSkuCreated(eventId, evt.skuId(), evt.productId());
            case "catalog.sku.activated" ->
                    stockCommands.handleSkuActivated(eventId, evt.skuId());
            case "catalog.sku.deactivated" ->
                    stockCommands.handleSkuDeactivated(eventId, evt.skuId());
            default -> log.warn("Unknown event type: {}", evt.eventType());
        }
    }

    private record CatalogEvent(UUID eventId, String eventType, UUID skuId, UUID productId) {}
}

