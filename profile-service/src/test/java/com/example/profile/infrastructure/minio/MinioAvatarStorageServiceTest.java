package com.example.profile.infrastructure.minio;

import com.example.common.web.error.ApiException;
import com.example.profile.config.AvatarStorageProperties;
import com.example.profile.domain.common.PresignedUrl;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MinioAvatarStorageServiceTest {

    private MinioClient client;
    private AvatarStorageProperties properties;
    private MinioAvatarStorageService service;

    @BeforeEach
    void setUp() {
        client = mock(MinioClient.class);
        properties = new AvatarStorageProperties();
        properties.setBucket("avatars");
        properties.setPrefix("tenant");
        properties.setUploadExpirySeconds(600);
        properties.setViewExpirySeconds(120);
        service = new MinioAvatarStorageService(client, properties);
    }

    @Test
    void prepareUpload_successEnsuresBucketAndReturnsUrl() throws Exception {
        UUID userId = UUID.randomUUID();
        when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(client.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("https://minio/upload");

        PresignedUrl url = service.prepareUpload(userId, "photo.jpg", "image/jpeg");

        assertThat(url.getUrl()).isEqualTo("https://minio/upload");
        assertThat(url.getMethod()).isEqualTo("PUT");
        assertThat(url.getHeaders()).containsEntry("Content-Type", "image/jpeg");
        assertThat(url.getObjectKey()).startsWith("tenant/")
                .contains(userId.toString())
                .endsWith(".jpg");
        assertThat(url.getExpiresAt()).isAfter(Instant.now());
        verify(client).bucketExists(any(BucketExistsArgs.class));
    }

    @Test
    void prepareUpload_withoutExtensionFallsBackToContentType() throws Exception {
        UUID userId = UUID.randomUUID();
        when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(client.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("https://minio/upload");

        PresignedUrl url = service.prepareUpload(userId, "avatar", "image/webp");

        assertThat(url.getObjectKey()).endsWith(".webp");
    }

    @Test
    void prepareUpload_bucketCreatedOnceWhenMissing() throws Exception {
        UUID userId = UUID.randomUUID();
        when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);
        when(client.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("https://minio/upload", "https://minio/upload2");

        PresignedUrl first = service.prepareUpload(userId, "file.png", "image/png");
        PresignedUrl second = service.prepareUpload(userId, "file2.png", "image/png");

        assertThat(first.getUrl()).isEqualTo("https://minio/upload");
        assertThat(second.getUrl()).isEqualTo("https://minio/upload2");
        verify(client, times(1)).bucketExists(any(BucketExistsArgs.class));
        verify(client, times(1)).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void prepareUpload_failureWrapsInApiException() throws Exception {
        UUID userId = UUID.randomUUID();
        when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(client.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> service.prepareUpload(userId, "file.png", "image/png"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("avatar_storage_unavailable");
    }

    @Test
    void prepareView_returnsUrlWhenAvailable() throws Exception {
        when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(client.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("https://minio/view");

        Optional<PresignedUrl> url = service.prepareView("tenant/object");

        assertThat(url).isPresent();
        assertThat(url.get().getMethod()).isEqualTo("GET");
        assertThat(url.get().getHeaders()).isEqualTo(Map.of());
        assertThat(url.get().getUrl()).isEqualTo("https://minio/view");
    }

    @Test
    void prepareView_blankObjectKeyReturnsEmpty() {
        assertThat(service.prepareView(null)).isEmpty();
        assertThat(service.prepareView(" ")).isEmpty();
        verifyNoInteractions(client);
    }

    @Test
    void prepareView_failureReturnsEmpty() throws Exception {
        when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(client.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenThrow(new RuntimeException("boom"));

        assertThat(service.prepareView("tenant/object")).isEmpty();
    }

    @Test
    void delete_blankKeyDoesNothing() throws Exception {
        service.delete(null);
        service.delete(" ");
        verifyNoInteractions(client);
    }

    @Test
    void delete_failuresAreSwallowed() throws Exception {
        doThrow(new RuntimeException("boom")).when(client).removeObject(any(RemoveObjectArgs.class));

        service.delete("tenant/object");

        verify(client).removeObject(any(RemoveObjectArgs.class));
    }
}
