package com.example.catalog.web.sku;

import com.example.catalog.application.sku.SkuCommands;
import com.example.catalog.security.ProductAccessEvaluator;
import com.example.catalog_service.web.api.SkuApi;
import com.example.catalog_service.web.model.Sku;
import com.example.catalog_service.web.model.SkuCreateRequest;
import com.example.catalog_service.web.model.SkuUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SkuController implements SkuApi {
    private final SkuCommands skuCommands;
    private final ProductAccessEvaluator productAccessEvaluator;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:sku:write') or @productAccessEvaluator.isOwner(authentication, #productId)")
    public ResponseEntity<Sku> createSku(UUID productId, @Valid SkuCreateRequest skuCreateRequest) {
        Authentication authentication = currentAuthentication();
        UUID actorId = productAccessEvaluator.requireCurrentActorId(authentication);
        boolean canOverride = hasOverridePrivileges(authentication, "catalog:sku:write");
        var s = skuCommands.create(
                actorId,
                productId,
                skuCreateRequest.getSkuCode(),
                skuCreateRequest.getActive(),
                skuCreateRequest.getBarcode(),
                canOverride
        );
        return ResponseEntity.status(201).body(map(s));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:sku:write') or @skuAccessEvaluator.isOwner(authentication, #id)")
    public ResponseEntity<Void> deleteSku(UUID id) {
        Authentication authentication = currentAuthentication();
        UUID actorId = productAccessEvaluator.requireCurrentActorId(authentication);
        boolean canOverride = hasOverridePrivileges(authentication, "catalog:sku:write");
        skuCommands.delete(actorId, id, canOverride);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:sku:write') or @skuAccessEvaluator.isOwner(authentication, #id)")
    public ResponseEntity<Sku> updateSku(UUID id, @Valid SkuUpdateRequest skuUpdateRequest) {
        Authentication authentication = currentAuthentication();
        UUID actorId = productAccessEvaluator.requireCurrentActorId(authentication);
        boolean canOverride = hasOverridePrivileges(authentication, "catalog:sku:write");
        var s = skuCommands.update(
                actorId,
                id,
                skuUpdateRequest.getSkuCode(),
                skuUpdateRequest.getActive(),
                skuUpdateRequest.getBarcode(),
                canOverride
        );
        return ResponseEntity.ok(map(s));
    }

    private static Sku map(com.example.catalog.domain.sku.Sku s) {
        return new Sku()
                .id(s.getId())
                .productId(s.getProductId())
                .skuCode(s.getSkuCode())
                .active(s.isActive())
                .barcode(s.getBarcode());
    }

    private Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean hasOverridePrivileges(Authentication authentication, String requiredAuthority) {
        if (authentication == null) {
            return false;
        }
        for (var authority : authentication.getAuthorities()) {
            String value = authority.getAuthority();
            if ("ROLE_ADMIN".equals(value) || "ROLE_CATALOG_EDITOR".equals(value) || value.equals(requiredAuthority)) {
                return true;
            }
        }
        return false;
    }
}
