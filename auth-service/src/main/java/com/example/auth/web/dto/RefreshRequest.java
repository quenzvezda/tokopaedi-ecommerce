package com.example.auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RefreshRequest {
    @NotBlank private String refresh_token;
}
