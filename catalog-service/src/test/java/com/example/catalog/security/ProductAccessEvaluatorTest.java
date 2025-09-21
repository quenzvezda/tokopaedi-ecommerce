package com.example.catalog.security;

import com.example.catalog.application.product.ProductQueries;
import com.example.catalog.domain.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductAccessEvaluatorTest {

    ProductQueries productQueries = mock(ProductQueries.class);
    ProductAccessEvaluator evaluator = new ProductAccessEvaluator(productQueries);

    @Test
    void isOwner_returnsTrueWhenSubjectMatches() {
        UUID productId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var product = Product.builder().id(productId).createdBy(owner).createdAt(Instant.now()).build();
        when(productQueries.getById(productId)).thenReturn(product);

        Authentication auth = jwtAuth(owner);

        assertThat(evaluator.isOwner(auth, productId)).isTrue();
    }

    @Test
    void isOwner_returnsFalseWhenSubjectDifferent() {
        UUID productId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var product = Product.builder().id(productId).createdBy(owner).build();
        when(productQueries.getById(productId)).thenReturn(product);

        Authentication auth = jwtAuth(UUID.randomUUID());

        assertThat(evaluator.isOwner(auth, productId)).isFalse();
    }

    @Test
    void isOwner_returnsFalseWhenLookupFails() {
        UUID productId = UUID.randomUUID();
        when(productQueries.getById(productId)).thenThrow(new RuntimeException("boom"));

        Authentication auth = jwtAuth(UUID.randomUUID());

        assertThat(evaluator.isOwner(auth, productId)).isFalse();
    }

    @Test
    void isOwner_returnsFalseForNonJwtAuthentication() {
        Authentication auth = mock(Authentication.class);
        assertThat(evaluator.isOwner(auth, UUID.randomUUID())).isFalse();
    }

    private static Authentication jwtAuth(UUID subject) {
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
