package com.example.profile.domain.profile;

import java.util.UUID;

public interface SellerRoleGateway {
    void ensureSellerRole(UUID accountId);
}
