package com.example.inventory.infrastructure.kafka;

import com.example.inventory.application.stock.StockCommands;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class CatalogEventListenerTest {

    StockCommands stockCommands = mock(StockCommands.class);
    ObjectMapper objectMapper = new ObjectMapper();
    CatalogEventListener listener = new CatalogEventListener(stockCommands, objectMapper);

    UUID eventId;
    UUID skuId;
    UUID productId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        skuId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    void handle_created_routesToCommand() throws Exception {
        String msg = String.format("{\"eventId\":\"%s\",\"eventType\":\"catalog.sku.created\",\"skuId\":\"%s\",\"productId\":\"%s\"}",
                eventId, skuId, productId);
        listener.handle(msg);
        verify(stockCommands).handleSkuCreated(eventId, skuId, productId);
    }

    @Test
    void handle_activated_routesToCommand() throws Exception {
        String msg = String.format("{\"eventId\":\"%s\",\"eventType\":\"catalog.sku.activated\",\"skuId\":\"%s\"}",
                eventId, skuId);
        listener.handle(msg);
        verify(stockCommands).handleSkuActivated(eventId, skuId);
    }

    @Test
    void handle_deactivated_routesToCommand() throws Exception {
        String msg = String.format("{\"eventId\":\"%s\",\"eventType\":\"catalog.sku.deactivated\",\"skuId\":\"%s\"}",
                eventId, skuId);
        listener.handle(msg);
        verify(stockCommands).handleSkuDeactivated(eventId, skuId);
    }

    @Test
    void handle_unknownEvent_logsAndSkips() throws Exception {
        String msg = String.format("{\"eventId\":\"%s\",\"eventType\":\"catalog.unknown\",\"skuId\":\"%s\"}",
                eventId, skuId);
        listener.handle(msg);
        verifyNoInteractions(stockCommands);
    }

    @Test
    void handle_invalidPayload_throws() {
        try {
            listener.handle("{not-json}");
        } catch (IllegalArgumentException e) {
            // expected
        }
        verifyNoInteractions(stockCommands);
    }
}
