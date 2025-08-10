package com.example.auth.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String token_type;
    private String access_token;
    private long expires_in;
    private String refresh_token;
}
