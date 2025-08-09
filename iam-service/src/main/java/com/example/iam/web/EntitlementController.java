package com.example.iam.web;

import com.example.iam.application.query.GetEntitlementsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/entitlements")
@RequiredArgsConstructor
public class EntitlementController {
    private final GetEntitlementsQuery query;

    @GetMapping("/{accountId}")
    public Map<String, Object> getEntitlements(@PathVariable("accountId") UUID accountId) { return query.handle(accountId); }
}
