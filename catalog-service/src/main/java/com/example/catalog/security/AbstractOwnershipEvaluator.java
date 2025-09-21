package com.example.catalog.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;
import java.util.UUID;

/**
 * Base evaluator that extracts the JWT subject as the acting user and compares it with
 * the owner information resolved from a resource identifier.
 */
public abstract class AbstractOwnershipEvaluator<ID> {

    /**
     * Resolve the current authenticated actor identifier from the provided authentication.
     *
     * @param authentication the authentication instance, typically a JWT token
     * @return optional containing the actor identifier if resolvable
     */
    public Optional<UUID> currentActorId(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken token) {
            String subject = token.getToken().getSubject();
            if (subject != null) {
                try {
                    return Optional.of(UUID.fromString(subject));
                } catch (IllegalArgumentException ignored) {
                    // fall through to empty optional
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Resolve the current authenticated actor identifier or throw if not available.
     *
     * @param authentication the authentication instance
     * @return the actor identifier
     * @throws IllegalStateException if the identifier cannot be resolved
     */
    public UUID requireCurrentActorId(Authentication authentication) {
        return currentActorId(authentication)
                .orElseThrow(() -> new IllegalStateException("Authenticated user subject is required"));
    }

    /**
     * Determine whether the current authentication represents the owner of the resource.
     *
     * @param authentication the authentication
     * @param resourceId     the resource identifier
     * @return {@code true} if the current actor owns the resource
     */
    public boolean isOwner(Authentication authentication, ID resourceId) {
        if (resourceId == null) {
            return false;
        }
        Optional<UUID> actorId = currentActorId(authentication);
        if (actorId.isEmpty()) {
            return false;
        }
        try {
            return loadOwnerId(resourceId)
                    .map(actorId.get()::equals)
                    .orElse(false);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    /**
     * Load the owner identifier for the provided resource.
     *
     * @param resourceId the resource identifier
     * @return optional owner identifier
     */
    protected abstract Optional<UUID> loadOwnerId(ID resourceId);
}
