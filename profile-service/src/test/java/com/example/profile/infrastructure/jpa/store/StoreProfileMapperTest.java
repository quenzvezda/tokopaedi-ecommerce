package com.example.profile.infrastructure.jpa.store;

import com.example.profile.domain.store.StoreProfile;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class StoreProfileMapperTest {

    @Test
    void toDomain_returnsNullWhenEntityNull() {
        assertThat(StoreProfileMapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsAllFields() {
        Instant now = Instant.now();
        StoreProfileEntity entity = new StoreProfileEntity();
        entity.setId(UUID.randomUUID());
        entity.setOwnerId(UUID.randomUUID());
        entity.setName("Shop");
        entity.setSlug("shop");
        entity.setDescription("desc");
        entity.setActive(true);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now.plusSeconds(5));

        StoreProfile profile = StoreProfileMapper.toDomain(entity);

        assertThat(profile.getId()).isEqualTo(entity.getId());
        assertThat(profile.getOwnerId()).isEqualTo(entity.getOwnerId());
        assertThat(profile.getName()).isEqualTo("Shop");
        assertThat(profile.getSlug()).isEqualTo("shop");
        assertThat(profile.getDescription()).isEqualTo("desc");
        assertThat(profile.isActive()).isTrue();
        assertThat(profile.getCreatedAt()).isEqualTo(now);
        assertThat(profile.getUpdatedAt()).isEqualTo(now.plusSeconds(5));
    }

    @Test
    void toEntity_returnsNullWhenProfileNull() {
        assertThat(StoreProfileMapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        Instant created = Instant.parse("2024-01-01T00:00:00Z");
        StoreProfile profile = StoreProfile.builder()
                .id(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .name("Shop")
                .slug("shop")
                .description("desc")
                .active(true)
                .createdAt(created)
                .updatedAt(created.plusSeconds(30))
                .build();

        StoreProfileEntity entity = StoreProfileMapper.toEntity(profile);

        assertThat(entity.getId()).isEqualTo(profile.getId());
        assertThat(entity.getOwnerId()).isEqualTo(profile.getOwnerId());
        assertThat(entity.getName()).isEqualTo("Shop");
        assertThat(entity.getSlug()).isEqualTo("shop");
        assertThat(entity.getDescription()).isEqualTo("desc");
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getCreatedAt()).isEqualTo(created);
        assertThat(entity.getUpdatedAt()).isEqualTo(created.plusSeconds(30));
    }
}
