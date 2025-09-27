package com.example.profile.infrastructure.jpa.store;

import com.example.profile.domain.store.StoreProfile;

public final class StoreProfileMapper {
    private StoreProfileMapper() {
    }

    public static StoreProfile toDomain(StoreProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return StoreProfile.builder()
                .id(entity.getId())
                .ownerId(entity.getOwnerId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static StoreProfileEntity toEntity(StoreProfile storeProfile) {
        if (storeProfile == null) {
            return null;
        }
        StoreProfileEntity entity = new StoreProfileEntity();
        entity.setId(storeProfile.getId());
        entity.setOwnerId(storeProfile.getOwnerId());
        entity.setName(storeProfile.getName());
        entity.setSlug(storeProfile.getSlug());
        entity.setDescription(storeProfile.getDescription());
        entity.setActive(storeProfile.isActive());
        entity.setCreatedAt(storeProfile.getCreatedAt());
        entity.setUpdatedAt(storeProfile.getUpdatedAt());
        return entity;
    }
}
