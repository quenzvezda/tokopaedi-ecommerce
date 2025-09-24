package com.example.profile.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "profile.iam")
public class ProfileIamProperties {
    private String baseUrl = "http://iam-service";
    private String rolesPath = "/iam/internal/v1/users/{accountId}/roles";
    private String assignRolePath = "/iam/api/v1/assign/user/{accountId}/role/{roleId}";
    private String listRolesPath = "/iam/api/v1/roles";
    private String internalAuthHeader = "X-Internal-Token";
    private String internalAuthValue;
    private String sellerRoleName = "SELLER";
    private Long sellerRoleId;
    private int requestTimeoutMs = 3000;
}
