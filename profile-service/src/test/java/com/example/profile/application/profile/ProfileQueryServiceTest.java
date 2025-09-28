package com.example.profile.application.profile;

import com.example.common.web.error.ApiException;
import com.example.profile.domain.avatar.AvatarStorageService;
import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.profile.UserProfileRepository;
import com.example.profile.domain.store.StoreProfile;
import com.example.profile.domain.store.StoreProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileQueryServiceTest {

    private UserProfileRepository userProfiles;
    private StoreProfileRepository storeProfiles;
    private AvatarStorageService avatarStorageService;
    private ProfileQueries service;

    @BeforeEach
    void setUp() {
        userProfiles = mock(UserProfileRepository.class);
        storeProfiles = mock(StoreProfileRepository.class);
        avatarStorageService = mock(AvatarStorageService.class);
        service = new ProfileQueryService(userProfiles, storeProfiles, avatarStorageService);
    }

    @Test
    void getByUserId_returnsProfile() {
        UUID userId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder().userId(userId).fullName("Alice").build();
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.of(profile));

        UserProfile result = service.getByUserId(userId);

        assertThat(result).isSameAs(profile);
    }

    @Test
    void getByUserId_missingThrowsNotFound() {
        UUID userId = UUID.randomUUID();
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByUserId(userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("profile_not_found");
    }

    @Test
    void listStores_delegatesToRepository() {
        UUID ownerId = UUID.randomUUID();
        List<StoreProfile> stores = List.of(StoreProfile.builder().ownerId(ownerId).name("Shop").build());
        when(storeProfiles.findByOwnerId(ownerId)).thenReturn(stores);

        List<StoreProfile> result = service.listStores(ownerId);

        assertThat(result).isEqualTo(stores);
    }

    @Test
    void getAvatarView_returnsPresignedUrl() {
        UUID userId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .avatarObjectKey("avatars/u.png")
                .build();
        PresignedUrl presignedUrl = PresignedUrl.builder()
                .url("https://cdn/avatar")
                .method("GET")
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(avatarStorageService.prepareView("avatars/u.png")).thenReturn(Optional.of(presignedUrl));

        PresignedUrl result = service.getAvatarView(userId);

        assertThat(result).isSameAs(presignedUrl);
    }

    @Test
    void getAvatarView_missingAvatarKeyThrowsNotFound() {
        UUID userId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder().userId(userId).avatarObjectKey(" ").build();
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> service.getAvatarView(userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("avatar_not_found");
        verifyNoInteractions(avatarStorageService);
    }

    @Test
    void getAvatarView_storageUnavailableThrowsServiceUnavailable() {
        UUID userId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .avatarObjectKey("avatars/u.png")
                .build();
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(avatarStorageService.prepareView("avatars/u.png")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAvatarView(userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("avatar_unavailable");
    }
}
