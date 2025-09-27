package com.example.profile.domain.avatar;

import com.example.profile.domain.common.PresignedUrl;

import java.util.Optional;
import java.util.UUID;

public interface AvatarStorageService {
    PresignedUrl prepareUpload(UUID userId, String fileName, String contentType);

    Optional<PresignedUrl> prepareView(String objectKey);

    void delete(String objectKey);
}
