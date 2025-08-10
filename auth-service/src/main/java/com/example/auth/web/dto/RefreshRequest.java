package com.example.auth.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RefreshRequest {
    @JsonProperty("refresh_token")
    @NotBlank
    private String refreshToken;
}
