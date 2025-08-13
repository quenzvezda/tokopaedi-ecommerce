package com.example.iam.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class AuthzCheckRequest {
    @NotNull private UUID sub;
    @NotBlank private String action;
}
