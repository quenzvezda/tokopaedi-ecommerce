package com.example.catalog.web.product;

import com.example.catalog.application.product.ProductCommands;
import com.example.catalog.application.product.ProductQueries;
import com.example.catalog_service.web.api.ProductApi;
import com.example.catalog_service.web.model.ProductCreateRequest;
import com.example.catalog_service.web.model.ProductDetail;
import com.example.catalog_service.web.model.ProductPage;
import com.example.catalog_service.web.model.ProductUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class ProductController implements ProductApi {
    private final ProductQueries productQueries;
    private final ProductCommands productCommands;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:product:create')")
    public ResponseEntity<ProductDetail> createProduct(@Valid ProductCreateRequest productCreateRequest) {
        UUID creatorId = currentUserId();
        var p = productCommands.create(
                creatorId,
                productCreateRequest.getName(),
                productCreateRequest.getShortDesc(),
                productCreateRequest.getBrandId(),
                productCreateRequest.getCategoryId(),
                productCreateRequest.getPublished()
        );
        return ResponseEntity.status(201).body(toDetail(p));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:product:delete')")
    public ResponseEntity<Void> deleteProduct(UUID id) {
        productCommands.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ProductDetail> getProductDetail(String slug) {
        return ResponseEntity.ok(toDetail(productQueries.getBySlug(slug)));
    }

    @Override
    public ResponseEntity<ProductPage> listProducts(String q, UUID brandId, UUID categoryId, Integer page, Integer size) {
        var pr = productQueries.search(q, brandId, categoryId, page == null ? 0 : page, size == null ? 20 : size);
        var content = pr.content().stream()
                .map(p -> new com.example.catalog_service.web.model.Product()
                        .id(p.getId() != null ? p.getId().toString() : null)
                        .name(p.getName())
                        .description(p.getShortDesc()))
                .toList();
        ProductPage body = new ProductPage()
                .content(content)
                .number(pr.page())
                .size(pr.size())
                .totalElements((int) Math.min(Integer.MAX_VALUE, Math.max(0, pr.totalElements())))
                .totalPages(pr.totalPages());
        return ResponseEntity.ok(body);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:product:update') or @productAccessEvaluator.isOwner(authentication, #id)")
    public ResponseEntity<ProductDetail> updateProduct(UUID id, @Valid ProductUpdateRequest productUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID actorId = currentUserId();
        boolean canOverride = hasOverridePrivileges(authentication);
        var p = productCommands.update(
                actorId,
                id,
                productUpdateRequest.getName(),
                productUpdateRequest.getShortDesc(),
                productUpdateRequest.getBrandId(),
                productUpdateRequest.getCategoryId(),
                productUpdateRequest.getPublished(),
                canOverride
        );
        return ResponseEntity.ok(toDetail(p));
    }

    private static ProductDetail toDetail(com.example.catalog.domain.product.Product p) {
        return new ProductDetail()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .shortDesc(p.getShortDesc())
                .brandId(p.getBrandId())
                .categoryId(p.getCategoryId())
                .published(p.isPublished())
                .createdBy(p.getCreatedBy())
                .createdAt(p.getCreatedAt() != null ? java.time.OffsetDateTime.ofInstant(p.getCreatedAt(), java.time.ZoneOffset.UTC) : null)
                .updatedAt(p.getUpdatedAt() != null ? java.time.OffsetDateTime.ofInstant(p.getUpdatedAt(), java.time.ZoneOffset.UTC) : null);
    }

    private static UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken token) {
            String subject = token.getToken().getSubject();
            if (subject != null) {
                try {
                    return UUID.fromString(subject);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalStateException("Authenticated subject is not a valid UUID", ex);
                }
            }
        }
        throw new IllegalStateException("Authenticated user subject is required");
    }

    private static boolean hasOverridePrivileges(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String value = authority.getAuthority();
            if ("ROLE_ADMIN".equals(value) || "ROLE_CATALOG_EDITOR".equals(value) || "catalog:product:update".equals(value)) {
                return true;
            }
        }
        return false;
    }
}
