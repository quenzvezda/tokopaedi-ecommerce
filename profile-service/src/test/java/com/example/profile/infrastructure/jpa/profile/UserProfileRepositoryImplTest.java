package com.example.profile.infrastructure.jpa.profile;

import com.example.profile.domain.profile.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileRepositoryImplTest {

    private JpaUserProfileRepository jpaRepository;
    private UserProfileRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(JpaUserProfileRepository.class);
        repository = new UserProfileRepositoryImpl(jpaRepository);
    }

    @Test
    void findByUserId_mapsEntity() {
        UUID userId = UUID.randomUUID();
        UserProfileEntity entity = new UserProfileEntity();
        entity.setUserId(userId);
        entity.setFullName("Alice");
        when(jpaRepository.findByUserId(userId)).thenReturn(Optional.of(entity));

        Optional<UserProfile> result = repository.findByUserId(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(userId);
    }

    @Test
    void findByUserId_returnsEmptyWhenMissing() {
        UUID userId = UUID.randomUUID();
        when(jpaRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<UserProfile> result = repository.findByUserId(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void save_convertsEntity() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .fullName("Alice")
                .bio("bio")
                .phone("123")
                .avatarObjectKey("avatars/a.png")
                .createdAt(now)
                .updatedAt(now.plusSeconds(1))
                .build();
        UserProfileEntity savedEntity = new UserProfileEntity();
        savedEntity.setUserId(userId);
        savedEntity.setFullName("Saved");
        savedEntity.setCreatedAt(now);
        savedEntity.setUpdatedAt(now.plusSeconds(2));
        when(jpaRepository.save(any(UserProfileEntity.class))).thenReturn(savedEntity);

        UserProfile result = repository.save(profile);

        ArgumentCaptor<UserProfileEntity> captor = ArgumentCaptor.forClass(UserProfileEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertThat(captor.getValue().getFullName()).isEqualTo("Alice");
        assertThat(result.getFullName()).isEqualTo("Saved");
        assertThat(result.getUpdatedAt()).isEqualTo(now.plusSeconds(2));
    }
}
