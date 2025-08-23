package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/entitlements")
@RequiredArgsConstructor
public class EntitlementController {
    private final EntitlementQueries queries;

    @GetMapping("/{accountId}")
    public Map<String, Object> getEntitlements(@PathVariable UUID accountId) {
        return queries.getEntitlements(accountId);
    }
}
