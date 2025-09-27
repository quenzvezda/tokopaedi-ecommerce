package com.example.profile.infrastructure.minio;

import com.example.common.web.error.ApiException;
import com.example.profile.config.AvatarStorageProperties;
import com.example.profile.domain.avatar.AvatarStorageService;
import com.example.profile.domain.common.PresignedUrl;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class MinioAvatarStorageService implements AvatarStorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioAvatarStorageService.class);

    private final MinioClient client;
    private final AvatarStorageProperties properties;
    private volatile boolean bucketEnsured = false;

    @Override
    public PresignedUrl prepareUpload(UUID userId, String fileName, String contentType) {
        ensureBucket();
        String extension = extensionFrom(fileName, contentType);
        String prefix = properties.getPrefix() == null ? "" : properties.getPrefix();
        if (!prefix.isEmpty() && !prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        String objectKey = prefix + userId + "/" + UUID.randomUUID() + extension;

        Map<String, String> headers = new HashMap<>();
        if (contentType != null && !contentType.isBlank()) {
            headers.put("Content-Type", contentType);
        }

        try {
            GetPresignedObjectUrlArgs.Builder builder = GetPresignedObjectUrlArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .method(Method.PUT)
                    .expiry((int) properties.getUploadExpirySeconds());
            if (!headers.isEmpty()) {
                builder.extraHeaders(headers);
            }
            String url = client.getPresignedObjectUrl(builder.build());
            Instant expiresAt = Instant.now().plus(properties.getUploadExpirySeconds(), ChronoUnit.SECONDS);
            return PresignedUrl.builder()
                    .url(url)
                    .method("PUT")
                    .expiresAt(expiresAt)
                    .headers(headers)
                    .objectKey(objectKey)
                    .build();
        } catch (Exception ex) {
            log.error("Failed to prepare upload URL for {}: {}", objectKey, ex.toString());
            throw ApiException.serviceUnavailable("avatar_storage_unavailable", "Unable to generate upload URL");
        }
    }

    @Override
    public Optional<PresignedUrl> prepareView(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return Optional.empty();
        }
        ensureBucket();
        try {
            String url = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .method(Method.GET)
                    .expiry((int) properties.getViewExpirySeconds())
                    .build());
            Instant expiresAt = Instant.now().plus(properties.getViewExpirySeconds(), ChronoUnit.SECONDS);
            return Optional.of(PresignedUrl.builder()
                    .url(url)
                    .method("GET")
                    .expiresAt(expiresAt)
                    .headers(Map.of())
                    .objectKey(objectKey)
                    .build());
        } catch (Exception ex) {
            log.error("Failed to prepare view URL for {}: {}", objectKey, ex.toString());
            return Optional.empty();
        }
    }

    @Override
    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .build());
        } catch (Exception ex) {
            log.warn("Failed to delete object {} from MinIO: {}", objectKey, ex.toString());
        }
    }

    private void ensureBucket() {
        if (bucketEnsured) {
            return;
        }
        synchronized (this) {
            if (bucketEnsured) {
                return;
            }
            try {
                boolean exists = client.bucketExists(BucketExistsArgs.builder()
                        .bucket(properties.getBucket())
                        .build());
                if (!exists) {
                    client.makeBucket(MakeBucketArgs.builder()
                            .bucket(properties.getBucket())
                            .build());
                }
                bucketEnsured = true;
            } catch (Exception ex) {
                log.error("Failed to ensure bucket {}: {}", properties.getBucket(), ex.toString());
                throw ApiException.serviceUnavailable("avatar_storage_unavailable", "Storage bucket unavailable");
            }
        }
    }

    private String extensionFrom(String fileName, String contentType) {
        if (fileName != null && fileName.contains(".")) {
            String ext = fileName.substring(fileName.lastIndexOf('.'));
            if (ext.length() <= 10) {
                return ext;
            }
        }
        if (contentType != null) {
            String normalized = contentType.toLowerCase();
            return switch (normalized) {
                case "image/jpeg", "image/jpg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> "";
            };
        }
        return "";
    }
}
