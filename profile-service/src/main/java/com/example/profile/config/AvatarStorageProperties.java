package com.example.profile.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "profile.avatar")
public class AvatarStorageProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private boolean secure = false;
    private String bucket = "user-avatars";
    private String prefix = "user-avatars/";
    private long uploadExpirySeconds = 900;
    private long viewExpirySeconds = 900;
}
