package com.example.profile.application.profile;

import com.example.common.web.error.ApiException;
import com.example.profile.domain.avatar.AvatarStorageService;
import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.profile.UserProfileRepository;
import com.example.profile.domain.store.StoreProfile;
import com.example.profile.domain.store.StoreProfileRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class ProfileQueryService implements ProfileQueries {

    private final UserProfileRepository userProfiles;
    private final StoreProfileRepository storeProfiles;
    private final AvatarStorageService avatarStorageService;

    @Override
    public UserProfile getByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        return userProfiles.findByUserId(userId)
                .orElseThrow(() -> ApiException.notFound("profile_not_found", "Profile not found"));
    }

    @Override
    public List<StoreProfile> listStores(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        return storeProfiles.findByOwnerId(ownerId);
    }

    @Override
    public PresignedUrl getAvatarView(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        UserProfile profile = getByUserId(userId);
        String objectKey = profile.getAvatarObjectKey();
        if (objectKey == null || objectKey.isBlank()) {
            throw ApiException.notFound("avatar_not_found", "Avatar not set");
        }
        return avatarStorageService.prepareView(objectKey)
                .orElseThrow(() -> ApiException.serviceUnavailable("avatar_unavailable", "Failed to generate avatar URL"));
    }
}
