package com.example.iam.domain.entitlement;

import lombok.Value;
import lombok.With;
import java.util.UUID;

@Value
@With
public class EntitlementVersion {
    Long id;
    UUID accountId;
    int version;

    public static EntitlementVersion init(UUID accountId) { return new EntitlementVersion(null, accountId, 1); }
    public EntitlementVersion bump() { return new EntitlementVersion(id, accountId, version + 1); }
}
