package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam.web.dto.AuthzCheckRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/authz")
@RequiredArgsConstructor
public class AuthzController {
    private final EntitlementQueries queries;

    @PostMapping("/check")
    public Map<String, Object> check(@Valid @RequestBody AuthzCheckRequest req) {
        return queries.checkAuthorization(req.getSub(), req.getAction());
    }
}
