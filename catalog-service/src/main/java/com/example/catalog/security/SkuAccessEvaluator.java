package com.example.catalog.security;

import com.example.catalog.application.product.ProductQueries;
import com.example.catalog.domain.sku.SkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("skuAccessEvaluator")
@RequiredArgsConstructor
public class SkuAccessEvaluator extends AbstractOwnershipEvaluator<UUID> {

    private final SkuRepository skuRepository;
    private final ProductQueries productQueries;

    @Override
    protected Optional<UUID> loadOwnerId(UUID skuId) {
        return skuRepository.findById(skuId)
                .map(com.example.catalog.domain.sku.Sku::getProductId)
                .flatMap(productId -> {
                    try {
                        var product = productQueries.getById(productId);
                        return Optional.ofNullable(product.getCreatedBy());
                    } catch (RuntimeException ex) {
                        return Optional.empty();
                    }
                });
    }
}
