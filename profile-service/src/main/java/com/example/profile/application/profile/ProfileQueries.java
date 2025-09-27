package com.example.profile.application.profile;

import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.store.StoreProfile;

import java.util.List;
import java.util.UUID;

public interface ProfileQueries {
    UserProfile getByUserId(UUID userId);

    List<StoreProfile> listStores(UUID ownerId);

    PresignedUrl getAvatarView(UUID userId);
}
