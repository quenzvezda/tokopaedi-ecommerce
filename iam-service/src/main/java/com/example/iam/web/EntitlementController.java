package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/entitlements")
@RequiredArgsConstructor
@Tag(name = "4. Entitlement")
public class EntitlementController {
    private final EntitlementQueries queries;

    @GetMapping("/{accountId}")
    @Operation(operationId = "entitlement_1_get", summary = "Get entitlements", security = {@SecurityRequirement(name = "bearer-key")})
    public Map<String, Object> getEntitlements(@PathVariable UUID accountId) {
        return queries.getEntitlements(accountId);
    }
}
