package com.example.catalog.web.dto;

import java.util.UUID;

public record BrandResponse(
        UUID id, String name, boolean active
) {}
