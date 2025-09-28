package com.example.profile.infrastructure.jpa.store;

import com.example.profile.domain.store.StoreProfile;
import com.example.profile.domain.store.StoreSlugAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreProfileRepositoryImplTest {

    private JpaStoreProfileRepository jpaRepository;
    private StoreProfileRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(JpaStoreProfileRepository.class);
        repository = new StoreProfileRepositoryImpl(jpaRepository);
    }

    @Test
    void save_conflictingSlugThrowsDomainException() {
        UUID ownerId = UUID.randomUUID();
        StoreProfile profile = StoreProfile.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .name("Shop")
                .slug("shop")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        ConstraintViolationException violation = new ConstraintViolationException(
                "duplicate",
                new SQLException("duplicate key"),
                "store_profiles_owner_slug_idx");
        when(jpaRepository.save(any(StoreProfileEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate", violation));

        assertThatThrownBy(() -> repository.save(profile))
                .isInstanceOf(StoreSlugAlreadyExistsException.class)
                .extracting("ownerId", "slug")
                .containsExactly(ownerId, "shop");
    }

    @Test
    void save_convertsToEntityAndBack() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        StoreProfile profile = StoreProfile.builder()
                .id(id)
                .ownerId(UUID.randomUUID())
                .name("Shop")
                .slug("shop")
                .description("desc")
                .active(true)
                .createdAt(now)
                .updatedAt(now.plusSeconds(5))
                .build();
        StoreProfileEntity savedEntity = new StoreProfileEntity();
        savedEntity.setId(id);
        savedEntity.setOwnerId(profile.getOwnerId());
        savedEntity.setName("Saved");
        savedEntity.setSlug("saved");
        savedEntity.setDescription("saved-desc");
        savedEntity.setActive(false);
        savedEntity.setCreatedAt(now);
        savedEntity.setUpdatedAt(now.plusSeconds(10));
        when(jpaRepository.save(any(StoreProfileEntity.class))).thenReturn(savedEntity);

        StoreProfile result = repository.save(profile);

        ArgumentCaptor<StoreProfileEntity> captor = ArgumentCaptor.forClass(StoreProfileEntity.class);
        verify(jpaRepository).save(captor.capture());
        StoreProfileEntity persisted = captor.getValue();
        assertThat(persisted.getId()).isEqualTo(profile.getId());
        assertThat(persisted.getOwnerId()).isEqualTo(profile.getOwnerId());
        assertThat(persisted.getName()).isEqualTo("Shop");
        assertThat(result.getName()).isEqualTo("Saved");
        assertThat(result.isActive()).isFalse();
    }

    @Test
    void findByOwnerId_mapsResultsToDomain() {
        UUID ownerId = UUID.randomUUID();
        StoreProfileEntity entity = new StoreProfileEntity();
        entity.setId(UUID.randomUUID());
        entity.setOwnerId(ownerId);
        entity.setName("Shop");
        entity.setSlug("shop");
        entity.setActive(true);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        when(jpaRepository.findByOwnerId(ownerId)).thenReturn(List.of(entity));

        List<StoreProfile> results = repository.findByOwnerId(ownerId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    void findByOwnerId_returnsEmptyListWhenNoneFound() {
        UUID ownerId = UUID.randomUUID();
        when(jpaRepository.findByOwnerId(ownerId)).thenReturn(List.of());

        List<StoreProfile> results = repository.findByOwnerId(ownerId);

        assertThat(results).isEmpty();
    }

    @Test
    void findByIdAndOwnerId_mapsOptional() {
        UUID ownerId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        StoreProfileEntity entity = new StoreProfileEntity();
        entity.setId(storeId);
        entity.setOwnerId(ownerId);
        when(jpaRepository.findByIdAndOwnerId(storeId, ownerId)).thenReturn(Optional.of(entity));

        Optional<StoreProfile> result = repository.findByIdAndOwnerId(storeId, ownerId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(storeId);
    }

    @Test
    void findByIdAndOwnerId_returnsEmptyWhenMissing() {
        UUID ownerId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        when(jpaRepository.findByIdAndOwnerId(storeId, ownerId)).thenReturn(Optional.empty());

        Optional<StoreProfile> result = repository.findByIdAndOwnerId(storeId, ownerId);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByOwnerId_delegatesToJpa() {
        UUID ownerId = UUID.randomUUID();
        when(jpaRepository.existsByOwnerId(ownerId)).thenReturn(true);

        assertThat(repository.existsByOwnerId(ownerId)).isTrue();
        verify(jpaRepository).existsByOwnerId(ownerId);
    }
}
