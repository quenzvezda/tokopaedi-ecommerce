package com.example.profile.application.profile;

import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.store.StoreProfile;

import java.util.UUID;

public interface ProfileCommands {

    UserProfile upsertProfile(UUID userId, UpdateProfileCommand command);

    PresignedUrl prepareAvatarUpload(UUID userId, AvatarUploadCommand command);

    StoreProfile createStore(UUID ownerId, CreateStoreCommand command);

    StoreProfile updateStore(UUID ownerId, UUID storeId, UpdateStoreCommand command);

    record UpdateProfileCommand(String fullName, String bio, String phone, String avatarObjectKey) {}

    record AvatarUploadCommand(String fileName, String contentType) {}

    record CreateStoreCommand(String name, String slug, String description) {}

    record UpdateStoreCommand(String name, String slug, String description, Boolean active) {}
}
