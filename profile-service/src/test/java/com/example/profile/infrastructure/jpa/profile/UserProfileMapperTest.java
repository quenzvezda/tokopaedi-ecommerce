package com.example.profile.infrastructure.jpa.profile;

import com.example.profile.domain.profile.UserProfile;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class UserProfileMapperTest {

    @Test
    void toDomain_returnsNullWhenEntityNull() {
        assertThat(UserProfileMapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsFields() {
        Instant created = Instant.now();
        UserProfileEntity entity = new UserProfileEntity();
        entity.setUserId(UUID.randomUUID());
        entity.setFullName("Alice");
        entity.setBio("bio");
        entity.setPhone("123");
        entity.setAvatarObjectKey("avatars/a.png");
        entity.setCreatedAt(created);
        entity.setUpdatedAt(created.plusSeconds(10));

        UserProfile profile = UserProfileMapper.toDomain(entity);

        assertThat(profile.getUserId()).isEqualTo(entity.getUserId());
        assertThat(profile.getFullName()).isEqualTo("Alice");
        assertThat(profile.getBio()).isEqualTo("bio");
        assertThat(profile.getPhone()).isEqualTo("123");
        assertThat(profile.getAvatarObjectKey()).isEqualTo("avatars/a.png");
        assertThat(profile.getCreatedAt()).isEqualTo(created);
        assertThat(profile.getUpdatedAt()).isEqualTo(created.plusSeconds(10));
    }

    @Test
    void toEntity_returnsNullWhenProfileNull() {
        assertThat(UserProfileMapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsFields() {
        Instant created = Instant.parse("2024-05-01T00:00:00Z");
        UserProfile profile = UserProfile.builder()
                .userId(UUID.randomUUID())
                .fullName("Alice")
                .bio("bio")
                .phone("123")
                .avatarObjectKey("avatars/a.png")
                .createdAt(created)
                .updatedAt(created.plusSeconds(5))
                .build();

        UserProfileEntity entity = UserProfileMapper.toEntity(profile);

        assertThat(entity.getUserId()).isEqualTo(profile.getUserId());
        assertThat(entity.getFullName()).isEqualTo("Alice");
        assertThat(entity.getBio()).isEqualTo("bio");
        assertThat(entity.getPhone()).isEqualTo("123");
        assertThat(entity.getAvatarObjectKey()).isEqualTo("avatars/a.png");
        assertThat(entity.getCreatedAt()).isEqualTo(created);
        assertThat(entity.getUpdatedAt()).isEqualTo(created.plusSeconds(5));
    }
}
