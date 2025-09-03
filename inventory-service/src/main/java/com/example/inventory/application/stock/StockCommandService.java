package com.example.inventory.application.stock;

import com.example.inventory.domain.stock.ProcessedEvent;
import com.example.inventory.domain.stock.ProcessedEventRepository;
import com.example.inventory.domain.stock.StockItem;
import com.example.inventory.domain.stock.StockItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
public class StockCommandService implements StockCommands {

    private final StockItemRepository stockRepo;
    private final ProcessedEventRepository eventRepo;

    @Override
    public void handleSkuCreated(UUID eventId, UUID skuId, UUID productId) {
        if (eventRepo.existsById(eventId)) return;

        stockRepo.findBySkuId(skuId).ifPresentOrElse(
                s -> {},
                () -> {
                    var now = Instant.now();
                    var item = StockItem.builder()
                            .skuId(skuId)
                            .productId(productId)
                            .qtyOnHand(0)
                            .reserved(0)
                            .sellable(false)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    stockRepo.save(item);
                }
        );

        eventRepo.save(new ProcessedEvent(eventId, "catalog.sku.created", Instant.now()));
    }

    @Override
    public void handleSkuActivated(UUID eventId, UUID skuId) {
        if (eventRepo.existsById(eventId)) return;

        var item = stockRepo.findBySkuId(skuId)
                .orElseGet(() -> StockItem.builder()
                        .skuId(skuId)
                        .qtyOnHand(0)
                        .reserved(0)
                        .sellable(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build());
        item.setSellable(true);
        item.setUpdatedAt(Instant.now());
        stockRepo.save(item);

        eventRepo.save(new ProcessedEvent(eventId, "catalog.sku.activated", Instant.now()));
    }

    @Override
    public void handleSkuDeactivated(UUID eventId, UUID skuId) {
        if (eventRepo.existsById(eventId)) return;

        var item = stockRepo.findBySkuId(skuId)
                .orElseGet(() -> StockItem.builder()
                        .skuId(skuId)
                        .qtyOnHand(0)
                        .reserved(0)
                        .sellable(false)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build());
        item.setSellable(false);
        item.setUpdatedAt(Instant.now());
        stockRepo.save(item);

        eventRepo.save(new ProcessedEvent(eventId, "catalog.sku.deactivated", Instant.now()));
    }
}

