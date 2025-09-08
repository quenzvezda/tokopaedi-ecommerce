package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam_service.web.api.AuthorizationApi;
import com.example.iam_service.web.model.AuthzCheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthzController implements AuthorizationApi {
    private final EntitlementQueries queries;

    @Override
    public ResponseEntity<Object> checkAuthorization(AuthzCheckRequest authzCheckRequest) {
        return ResponseEntity.ok((Object)queries.checkAuthorization(authzCheckRequest.getSub(), authzCheckRequest.getAction()));
    }
}
