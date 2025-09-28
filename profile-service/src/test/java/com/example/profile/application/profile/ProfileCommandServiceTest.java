package com.example.profile.application.profile;

import com.example.common.web.error.ApiException;
import com.example.profile.application.profile.ProfileCommands.AvatarUploadCommand;
import com.example.profile.application.profile.ProfileCommands.CreateInitialProfileCommand;
import com.example.profile.application.profile.ProfileCommands.CreateStoreCommand;
import com.example.profile.application.profile.ProfileCommands.UpdateProfileCommand;
import com.example.profile.application.profile.ProfileCommands.UpdateStoreCommand;
import com.example.profile.domain.avatar.AvatarStorageService;
import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.domain.profile.SellerRoleGateway;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.profile.UserProfileRepository;
import com.example.profile.domain.store.StoreProfile;
import com.example.profile.domain.store.StoreProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileCommandServiceTest {

    private UserProfileRepository userProfiles;
    private StoreProfileRepository storeProfiles;
    private AvatarStorageService avatarStorageService;
    private SellerRoleGateway sellerRoleGateway;
    private ProfileCommands service;

    @BeforeEach
    void setUp() {
        userProfiles = mock(UserProfileRepository.class);
        storeProfiles = mock(StoreProfileRepository.class);
        avatarStorageService = mock(AvatarStorageService.class);
        sellerRoleGateway = mock(SellerRoleGateway.class);
        service = new ProfileCommandService(userProfiles, storeProfiles, avatarStorageService, sellerRoleGateway);
    }

    @Test
    void upsertProfile_createsNewProfileWhenAbsent() {
        UUID userId = UUID.randomUUID();
        UpdateProfileCommand command = new UpdateProfileCommand("Alice", "Bio", "123", "avatar-key");
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfiles.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserProfile result = service.upsertProfile(userId, command);

        assertThat(result.getFullName()).isEqualTo("Alice");
        assertThat(result.getBio()).isEqualTo("Bio");
        assertThat(result.getPhone()).isEqualTo("123");
        assertThat(result.getAvatarObjectKey()).isEqualTo("avatar-key");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(avatarStorageService, never()).delete(anyString());
    }

    @Test
    void upsertProfile_updatesExistingAndDeletesPreviousAvatarWhenChanged() {
        UUID userId = UUID.randomUUID();
        Instant created = Instant.parse("2024-01-01T00:00:00Z");
        UserProfile existing = UserProfile.builder()
                .userId(userId)
                .fullName("Old")
                .bio("Old bio")
                .phone("000")
                .avatarObjectKey("old-avatar")
                .createdAt(created)
                .updatedAt(created)
                .build();
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(userProfiles.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileCommand command = new UpdateProfileCommand("New", "New bio", "999", "new-avatar");
        UserProfile result = service.upsertProfile(userId, command);

        assertThat(result.getFullName()).isEqualTo("New");
        assertThat(result.getBio()).isEqualTo("New bio");
        assertThat(result.getPhone()).isEqualTo("999");
        assertThat(result.getAvatarObjectKey()).isEqualTo("new-avatar");
        assertThat(result.getCreatedAt()).isEqualTo(created);
        assertThat(result.getUpdatedAt()).isNotNull().isAfterOrEqualTo(created);
        verify(avatarStorageService).delete("old-avatar");
    }

    @Test
    void upsertProfile_sameAvatar_skipsDeletion() {
        UUID userId = UUID.randomUUID();
        UserProfile existing = UserProfile.builder()
                .userId(userId)
                .avatarObjectKey("avatar")
                .createdAt(Instant.now().minusSeconds(60))
                .updatedAt(Instant.now().minusSeconds(60))
                .build();
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(userProfiles.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileCommand command = new UpdateProfileCommand("Alice", null, null, "avatar");
        service.upsertProfile(userId, command);

        verify(avatarStorageService, never()).delete(anyString());
    }

    @Test
    void createInitialProfile_returnsExistingWithoutSaving() {
        UUID userId = UUID.randomUUID();
        UserProfile existing = UserProfile.builder().userId(userId).fullName("Existing").build();
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.of(existing));

        UserProfile result = service.createInitialProfile(userId, new CreateInitialProfileCommand("Alice", "123"));

        assertThat(result).isSameAs(existing);
        verify(userProfiles, never()).save(any());
    }

    @Test
    void createInitialProfile_createsProfileWhenMissing() {
        UUID userId = UUID.randomUUID();
        when(userProfiles.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfiles.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserProfile result = service.createInitialProfile(userId, new CreateInitialProfileCommand("Alice", "123"));

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getFullName()).isEqualTo("Alice");
        assertThat(result.getPhone()).isEqualTo("123");
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void prepareAvatarUpload_delegatesToStorage() {
        UUID userId = UUID.randomUUID();
        PresignedUrl url = PresignedUrl.builder().url("https://upload").build();
        when(avatarStorageService.prepareUpload(eq(userId), anyString(), anyString())).thenReturn(url);

        PresignedUrl result = service.prepareAvatarUpload(userId, new AvatarUploadCommand("avatar.png", "image/png"));

        assertThat(result).isSameAs(url);
    }

    @Test
    void createStore_assignsSellerRoleWhenFirstStore() {
        UUID ownerId = UUID.randomUUID();
        when(storeProfiles.existsByOwnerId(ownerId)).thenReturn(false);
        when(storeProfiles.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StoreProfile result = service.createStore(ownerId, new CreateStoreCommand("Shop", "shop", "desc"));

        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        assertThat(result.getName()).isEqualTo("Shop");
        assertThat(result.getSlug()).isEqualTo("shop");
        assertThat(result.getDescription()).isEqualTo("desc");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getId()).isNotNull();
        verify(sellerRoleGateway).ensureSellerRole(ownerId);
    }

    @Test
    void createStore_existingStoreSkipsRoleAssignment() {
        UUID ownerId = UUID.randomUUID();
        when(storeProfiles.existsByOwnerId(ownerId)).thenReturn(true);
        when(storeProfiles.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createStore(ownerId, new CreateStoreCommand("Shop", "shop", null));

        verifyNoInteractions(sellerRoleGateway);
    }

    @Test
    void createStore_gatewayThrowsApiException_propagates() {
        UUID ownerId = UUID.randomUUID();
        when(storeProfiles.existsByOwnerId(ownerId)).thenReturn(false);
        when(storeProfiles.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ApiException upstream = ApiException.conflict("role_conflict", "role conflict");
        doThrow(upstream).when(sellerRoleGateway).ensureSellerRole(ownerId);

        assertThatThrownBy(() -> service.createStore(ownerId, new CreateStoreCommand("Shop", "shop", null)))
                .isSameAs(upstream);
    }

    @Test
    void createStore_gatewayThrowsRuntime_wrapsWithServiceUnavailable() {
        UUID ownerId = UUID.randomUUID();
        when(storeProfiles.existsByOwnerId(ownerId)).thenReturn(false);
        when(storeProfiles.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new IllegalStateException("oops")).when(sellerRoleGateway).ensureSellerRole(ownerId);

        assertThatThrownBy(() -> service.createStore(ownerId, new CreateStoreCommand("Shop", "shop", null)))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("iam_unavailable");
    }

    @Test
    void updateStore_updatesEditableFields() {
        UUID ownerId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Instant created = Instant.parse("2024-01-01T00:00:00Z");
        StoreProfile store = StoreProfile.builder()
                .id(storeId)
                .ownerId(ownerId)
                .name("Old")
                .slug("old")
                .description("old")
                .active(false)
                .createdAt(created)
                .updatedAt(created)
                .build();
        when(storeProfiles.findByIdAndOwnerId(storeId, ownerId)).thenReturn(Optional.of(store));
        ArgumentCaptor<StoreProfile> captor = ArgumentCaptor.forClass(StoreProfile.class);
        when(storeProfiles.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        StoreProfile result = service.updateStore(ownerId, storeId, new UpdateStoreCommand("New", "new", "desc", true));

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getSlug()).isEqualTo("new");
        assertThat(result.getDescription()).isEqualTo("desc");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getUpdatedAt()).isNotNull().isAfter(created);
        assertThat(captor.getValue().getUpdatedAt()).isEqualTo(result.getUpdatedAt());
    }

    @Test
    void updateStore_notFoundThrowsApiException() {
        UUID ownerId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        when(storeProfiles.findByIdAndOwnerId(storeId, ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStore(ownerId, storeId, new UpdateStoreCommand(null, null, null, null)))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("store_not_found");
    }
}
