package com.example.auth.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessTokenResponse {
    private String tokenType;
    private String accessToken;
    private long expiresIn;
}

