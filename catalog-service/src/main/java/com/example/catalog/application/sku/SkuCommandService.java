package com.example.catalog.application.sku;

import com.example.catalog.application.sku.events.SkuActivated;
import com.example.catalog.application.sku.events.SkuCreated;
import com.example.catalog.application.sku.events.SkuDeactivated;
import com.example.catalog.domain.sku.Sku;
import com.example.catalog.domain.sku.SkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
public class SkuCommandService implements SkuCommands {

    private final SkuRepository repo;
    private final ApplicationEventPublisher events;

    @Override
    public Sku create(UUID productId, String skuCode, Boolean active, String barcode) {
        Sku s = Sku.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .skuCode(skuCode)
                .active(active != null ? active : true)
                .barcode(barcode)
                .build();
        s = repo.save(s);

        // Publish domain events
        events.publishEvent(new SkuCreated(s.getId(), s.getProductId(), s.isActive()));
        if (s.isActive()) {
            events.publishEvent(new SkuActivated(s.getId()));
        }

        return s;
    }

    @Override
    public Sku update(UUID id, String skuCode, Boolean active, String barcode) {
        Sku current = repo.findById(id).orElseThrow();
        boolean wasActive = current.isActive();

        current.setSkuCode(skuCode != null ? skuCode : current.getSkuCode());
        if (active != null) current.setActive(active);
        current.setBarcode(barcode != null ? barcode : current.getBarcode());

        current = repo.save(current);

        // Publish activation state changes
        if (!wasActive && current.isActive()) {
            events.publishEvent(new SkuActivated(current.getId()));
        } else if (wasActive && !current.isActive()) {
            events.publishEvent(new SkuDeactivated(current.getId()));
        }

        return current;
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }
}

