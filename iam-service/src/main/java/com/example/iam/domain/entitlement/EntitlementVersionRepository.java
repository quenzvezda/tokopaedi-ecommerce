package com.example.iam.domain.entitlement;

import java.util.UUID;

public interface EntitlementVersionRepository {
    int getOrInit(UUID accountId);
    void bump(UUID accountId);
}
