package com.example.profile.web.mapper;

import com.example.profile.domain.common.PresignedUrl;
import com.example.profile.domain.profile.UserProfile;
import com.example.profile.domain.store.StoreProfile;
import com.example.profile_service.web.model.PresignedUrlResponse;
import com.example.profile_service.web.model.StoreProfileResponse;
import com.example.profile_service.web.model.UserProfileResponse;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

public final class ProfileMapper {
    private ProfileMapper() {
    }

    public static UserProfileResponse toUserProfileResponse(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        return new UserProfileResponse()
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .bio(profile.getBio())
                .phone(profile.getPhone())
                .avatarObjectKey(profile.getAvatarObjectKey())
                .createdAt(toOffsetDateTime(profile.getCreatedAt()))
                .updatedAt(toOffsetDateTime(profile.getUpdatedAt()));
    }

    public static StoreProfileResponse toStoreProfileResponse(StoreProfile storeProfile) {
        if (storeProfile == null) {
            return null;
        }
        return new StoreProfileResponse()
                .id(storeProfile.getId())
                .ownerId(storeProfile.getOwnerId())
                .name(storeProfile.getName())
                .slug(storeProfile.getSlug())
                .description(storeProfile.getDescription())
                .active(storeProfile.isActive())
                .createdAt(toOffsetDateTime(storeProfile.getCreatedAt()))
                .updatedAt(toOffsetDateTime(storeProfile.getUpdatedAt()));
    }

    public static PresignedUrlResponse toPresignedUrlResponse(PresignedUrl url) {
        if (url == null) {
            return null;
        }
        Map<String, String> headers = url.getHeaders();
        PresignedUrlResponse.MethodEnum methodEnum = url.getMethod() == null
                ? null
                : PresignedUrlResponse.MethodEnum.fromValue(url.getMethod());
        return new PresignedUrlResponse()
                .url(url.getUrl())
                .method(methodEnum)
                .expiresAt(toOffsetDateTime(url.getExpiresAt()))
                .headers(headers == null ? Map.of() : headers)
                .objectKey(url.getObjectKey());
    }

    private static OffsetDateTime toOffsetDateTime(java.time.Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
