package com.example.profile.domain.store;

import java.util.UUID;

/**
 * Thrown when trying to create or update a store with a slug that already exists for the same owner.
 */
public class StoreSlugAlreadyExistsException extends RuntimeException {

    private final UUID ownerId;
    private final String slug;

    public StoreSlugAlreadyExistsException(UUID ownerId, String slug, Throwable cause) {
        super(buildMessage(ownerId, slug), cause);
        this.ownerId = ownerId;
        this.slug = slug;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getSlug() {
        return slug;
    }

    private static String buildMessage(UUID ownerId, String slug) {
        return "Store slug already exists for owner " + ownerId + ": " + slug;
    }
}
