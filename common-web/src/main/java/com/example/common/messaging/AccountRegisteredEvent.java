package com.example.common.messaging;

import java.util.UUID;

/**
 * Integration event published whenever a new account is registered in auth-service.
 * <p>
 * The payload is intentionally minimal and shared across services via the common-web module
 * to avoid schema drift between producers and consumers.
 */
public record AccountRegisteredEvent(
        UUID accountId,
        String username,
        String email,
        String fullName,
        String phone
) {
}
