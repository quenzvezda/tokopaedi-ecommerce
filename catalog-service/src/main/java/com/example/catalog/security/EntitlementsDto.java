package com.example.catalog.security;

import java.util.List;

/**
 * DTO entitlements dari IAM.
 */
public record EntitlementsDto(
        Integer perm_ver,
        List<String> scopes
) {}
