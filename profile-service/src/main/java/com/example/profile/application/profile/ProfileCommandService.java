package com.example.profile.application.profile;

import com.example.common.web.error.ApiException;
import com.example.profile.application.profile.ProfileCommands.AvatarUploadCommand;
import com.example.profile.application.profile.ProfileCommands.CreateStoreCommand;
import com.example.profile.application.profile.ProfileCommands.UpdateStoreCommand;
import com.example.profile.domain.avatar.AvatarStorageService;
import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.domain.profile.SellerRoleGateway;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.profile.UserProfileRepository;
import com.example.profile.domain.store.StoreProfile;
import com.example.profile.domain.store.StoreProfileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class ProfileCommandService implements ProfileCommands {

    private static final Logger log = LoggerFactory.getLogger(ProfileCommandService.class);

    private final UserProfileRepository userProfiles;
    private final StoreProfileRepository storeProfiles;
    private final AvatarStorageService avatarStorageService;
    private final SellerRoleGateway sellerRoleGateway;

    @Override
    @Transactional
    public UserProfile upsertProfile(UUID userId, UpdateProfileCommand command) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(command, "command");
        Instant now = Instant.now();

        UserProfile profile = userProfiles.findByUserId(userId)
                .orElseGet(() -> UserProfile.builder()
                        .userId(userId)
                        .createdAt(now)
                        .updatedAt(now)
                        .fullName(command.fullName())
                        .bio(command.bio())
                        .phone(command.phone())
                        .avatarObjectKey(command.avatarObjectKey())
                        .build());

        String previousAvatar = profile.getAvatarObjectKey();
        profile.setFullName(command.fullName());
        profile.setBio(command.bio());
        profile.setPhone(command.phone());
        profile.setAvatarObjectKey(command.avatarObjectKey());
        profile.setUpdatedAt(now);
        if (profile.getCreatedAt() == null) {
            profile.setCreatedAt(now);
        }

        UserProfile saved = userProfiles.save(profile);

        if (previousAvatar != null && (command.avatarObjectKey() == null || !previousAvatar.equals(command.avatarObjectKey()))) {
            try {
                avatarStorageService.delete(previousAvatar);
            } catch (Exception ex) {
                log.warn("Failed to delete previous avatar {}: {}", previousAvatar, ex.toString());
            }
        }

        return saved;
    }

    @Override
    @Transactional
    public UserProfile createInitialProfile(UUID userId, CreateInitialProfileCommand command) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(command, "command");

        return userProfiles.findByUserId(userId)
                .orElseGet(() -> {
                    Instant now = Instant.now();
                    UserProfile profile = UserProfile.builder()
                            .userId(userId)
                            .fullName(command.fullName())
                            .phone(command.phone())
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return userProfiles.save(profile);
                });
    }

    @Override
    public PresignedUrl prepareAvatarUpload(UUID userId, AvatarUploadCommand command) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(command, "command");
        return avatarStorageService.prepareUpload(userId, command.fileName(), command.contentType());
    }

    @Override
    @Transactional
    public StoreProfile createStore(UUID ownerId, CreateStoreCommand command) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(command, "command");

        Instant now = Instant.now();
        boolean hasExistingStore = storeProfiles.existsByOwnerId(ownerId);

        StoreProfile store = StoreProfile.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .name(command.name())
                .slug(command.slug())
                .description(command.description())
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        StoreProfile saved = storeProfiles.save(store);

        if (!hasExistingStore) {
            try {
                sellerRoleGateway.ensureSellerRole(ownerId);
            } catch (ApiException ex) {
                throw ex;
            } catch (Exception ex) {
                throw ApiException.serviceUnavailable("iam_unavailable", "Failed to assign seller role");
            }
        }

        return saved;
    }

    @Override
    @Transactional
    public StoreProfile updateStore(UUID ownerId, UUID storeId, UpdateStoreCommand command) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(storeId, "storeId");
        Objects.requireNonNull(command, "command");

        StoreProfile store = storeProfiles.findByIdAndOwnerId(storeId, ownerId)
                .orElseThrow(() -> ApiException.notFound("store_not_found", "Store not found"));

        if (command.name() != null) {
            store.setName(command.name());
        }
        if (command.slug() != null) {
            store.setSlug(command.slug());
        }
        if (command.description() != null) {
            store.setDescription(command.description());
        }
        if (command.active() != null) {
            store.setActive(command.active());
        }
        store.setUpdatedAt(Instant.now());

        return storeProfiles.save(store);
    }
}
