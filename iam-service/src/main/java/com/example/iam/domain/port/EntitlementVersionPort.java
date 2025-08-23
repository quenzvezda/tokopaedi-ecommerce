package com.example.iam.domain.port;

import java.util.UUID;

public interface EntitlementVersionPort {
    int getOrInit(UUID accountId);
    void bump(UUID accountId);
}
