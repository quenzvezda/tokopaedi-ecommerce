package com.example.auth.domain.entitlement;

import java.util.UUID;

public interface EntitlementClient {
    Entitlements fetchEntitlements(UUID accountId);
}
