package com.example.iam.domain.model;

import lombok.Value;
import java.util.UUID;

@Value
public class UserRole {
    Long id;
    UUID accountId;
    Long roleId;

    public static UserRole of(UUID accountId, Long roleId) { return new UserRole(null, accountId, roleId); }
}
