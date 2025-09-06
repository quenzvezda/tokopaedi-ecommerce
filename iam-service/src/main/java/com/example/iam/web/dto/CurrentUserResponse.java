package com.example.iam.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CurrentUserResponse(
        UUID id,
        String username,
        String email,
        List<String> roles,
        List<String> permissions
) {}

