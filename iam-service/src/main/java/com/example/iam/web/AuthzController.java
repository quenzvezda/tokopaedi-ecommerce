package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam.web.dto.AuthzCheckRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/authz")
@RequiredArgsConstructor
@Tag(name = "5. Authorization")
public class AuthzController {
    private final EntitlementQueries queries;

    @PostMapping("/check")
    @Operation(operationId = "authz_1_check", summary = "Check authorization", security = {@SecurityRequirement(name = "bearer-key")})
    public Map<String, Object> check(@Valid @RequestBody AuthzCheckRequest req) {
        return queries.checkAuthorization(req.getSub(), req.getAction());
    }
}
