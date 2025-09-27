package com.example.profile.infrastructure.jpa.profile;

import com.example.profile.domain.profile.UserProfile;

public final class UserProfileMapper {
    private UserProfileMapper() {
    }

    public static UserProfile toDomain(UserProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserProfile.builder()
                .userId(entity.getUserId())
                .fullName(entity.getFullName())
                .bio(entity.getBio())
                .phone(entity.getPhone())
                .avatarObjectKey(entity.getAvatarObjectKey())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static UserProfileEntity toEntity(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        UserProfileEntity entity = new UserProfileEntity();
        entity.setUserId(profile.getUserId());
        entity.setFullName(profile.getFullName());
        entity.setBio(profile.getBio());
        entity.setPhone(profile.getPhone());
        entity.setAvatarObjectKey(profile.getAvatarObjectKey());
        entity.setCreatedAt(profile.getCreatedAt());
        entity.setUpdatedAt(profile.getUpdatedAt());
        return entity;
    }
}
