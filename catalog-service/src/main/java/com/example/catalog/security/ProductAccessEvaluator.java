package com.example.catalog.security;

import com.example.catalog.application.product.ProductQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Evaluator untuk memastikan hanya pemilik produk (atau caller override) yang boleh update.
 */
@Component("productAccessEvaluator")
@RequiredArgsConstructor
public class ProductAccessEvaluator extends AbstractOwnershipEvaluator<UUID> {

    private final ProductQueries productQueries;

    @Override
    protected Optional<UUID> loadOwnerId(UUID productId) {
        var product = productQueries.getById(productId);
        return Optional.ofNullable(product.getCreatedBy());
    }
}
