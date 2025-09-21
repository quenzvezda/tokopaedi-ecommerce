package com.example.catalog.security;

import com.example.catalog.application.product.ProductQueries;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.sku.Sku;
import com.example.catalog.domain.sku.SkuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SkuAccessEvaluatorTest {

    SkuRepository skuRepository = mock(SkuRepository.class);
    ProductQueries productQueries = mock(ProductQueries.class);
    SkuAccessEvaluator evaluator = new SkuAccessEvaluator(skuRepository, productQueries);

    @Test
    void isOwner_returnsTrueWhenSkuBelongsToActorProduct() {
        UUID skuId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        when(skuRepository.findById(skuId)).thenReturn(Optional.of(Sku.builder().id(skuId).productId(productId).build()));
        when(productQueries.getById(productId)).thenReturn(Product.builder().id(productId).createdBy(ownerId).createdAt(Instant.now()).build());

        Authentication auth = jwt(ownerId);

        assertThat(evaluator.isOwner(auth, skuId)).isTrue();
    }

    @Test
    void isOwner_returnsFalseWhenProductLookupFails() {
        UUID skuId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(skuRepository.findById(skuId)).thenReturn(Optional.of(Sku.builder().id(skuId).productId(productId).build()));
        when(productQueries.getById(productId)).thenThrow(new RuntimeException("not found"));

        Authentication auth = jwt(UUID.randomUUID());

        assertThat(evaluator.isOwner(auth, skuId)).isFalse();
    }

    @Test
    void isOwner_returnsFalseWhenSkuMissing() {
        UUID skuId = UUID.randomUUID();
        when(skuRepository.findById(skuId)).thenReturn(Optional.empty());

        Authentication auth = jwt(UUID.randomUUID());

        assertThat(evaluator.isOwner(auth, skuId)).isFalse();
    }

    private static Authentication jwt(UUID subject) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject.toString())
                .claim("sub", subject.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        return new JwtAuthenticationToken(jwt);
    }
}
