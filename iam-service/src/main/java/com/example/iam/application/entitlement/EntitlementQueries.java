package com.example.iam.application.entitlement;

import java.util.Map;
import java.util.UUID;

public interface EntitlementQueries {
    Map<String, Object> getEntitlements(UUID accountId);
    Map<String, Object> checkAuthorization(UUID sub, String action);
}
