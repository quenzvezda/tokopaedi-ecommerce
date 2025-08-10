package com.example.auth.domain.port;

import com.example.auth.domain.model.Entitlements;

import java.util.UUID;

public interface IamPort {
    Entitlements fetchEntitlements(UUID accountId);
}
