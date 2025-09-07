package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam_service.web.api.EntitlementApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EntitlementController implements EntitlementApi {
    private final EntitlementQueries queries;

    @Override
    public ResponseEntity<Object> getEntitlements(UUID accountId) {
        return ResponseEntity.ok((Object)queries.getEntitlements(accountId));
    }
}
