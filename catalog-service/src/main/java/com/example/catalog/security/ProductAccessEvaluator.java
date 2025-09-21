package com.example.catalog.security;

import com.example.catalog.application.product.ProductQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Evaluator untuk memastikan hanya pemilik produk (atau caller override) yang boleh update.
 */
@Component("productAccessEvaluator")
@RequiredArgsConstructor
public class ProductAccessEvaluator {

    private final ProductQueries productQueries;

    public boolean isOwner(Authentication authentication, UUID productId) {
        if (!(authentication instanceof JwtAuthenticationToken token)) {
            return false;
        }
        String subject = token.getToken().getSubject();
        if (subject == null) {
            return false;
        }
        UUID actorId;
        try {
            actorId = UUID.fromString(subject);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        try {
            var product = productQueries.getById(productId);
            UUID ownerId = product.getCreatedBy();
            return ownerId != null && ownerId.equals(actorId);
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
