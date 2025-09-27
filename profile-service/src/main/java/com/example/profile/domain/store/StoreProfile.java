package com.example.profile.domain.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreProfile {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String slug;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
