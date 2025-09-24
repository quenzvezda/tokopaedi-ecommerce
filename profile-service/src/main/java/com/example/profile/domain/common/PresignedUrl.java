package com.example.profile.domain.common;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

/**
 * Value object representing a pre-signed URL produced by storage service.
 */
@Value
@Builder
public class PresignedUrl {
    String url;
    String method;
    Instant expiresAt;
    Map<String, String> headers;
    String objectKey;
}
