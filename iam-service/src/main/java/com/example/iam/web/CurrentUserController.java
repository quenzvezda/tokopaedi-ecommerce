package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam.web.dto.CurrentUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "6. User")
public class CurrentUserController {

    private final EntitlementQueries entitlements;

    @GetMapping("/me")
    @Operation(
            operationId = "user_0_me",
            summary = "Get current authenticated user",
            description = "Returns the current user's id, username, email, roles, and permissions (SCOPE_*)",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current user",
                    content = @Content(
                            schema = @Schema(implementation = CurrentUserResponse.class),
                            examples = @ExampleObject(value = "{\n  \"id\": \"b2fd8e06-9e0a-4b7c-9e9e-2a9a2bdbb1fc\",\n  \"username\": \"alice\",\n  \"email\": \"alice@example.com\",\n  \"roles\": [\"ADMIN\", \"USER\"],\n  \"permissions\": [\"SCOPE_product:product:write\", \"SCOPE_catalog:brand:write\"]\n}"))
            )
    })
    public CurrentUserResponse me(@AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        UUID id = UUID.fromString(jwt.getSubject());
        String username = jwt.getClaimAsString("username");
        String email = jwt.getClaimAsString("email");

        // Roles: prefer JWT claim "roles"; fallback to authorities ROLE_*
        List<String> roles = Optional.ofNullable(jwt.getClaimAsStringList("roles"))
                .filter(list -> !list.isEmpty())
                .orElseGet(() -> authentication == null ? List.of() : authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(a -> a != null && a.startsWith("ROLE_"))
                        .map(a -> a.substring("ROLE_".length()))
                        .filter(s -> !s.isBlank())
                        .distinct()
                        .sorted()
                        .toList());

        // Permissions: fetch entitlements and normalize to SCOPE_* prefix for FE
        Map<String, Object> ent = entitlements.getEntitlements(id);
        @SuppressWarnings("unchecked")
        List<String> scopes = (List<String>) ent.getOrDefault("scopes", List.of());
        List<String> permissions = scopes == null ? List.of() : scopes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.startsWith("SCOPE_") ? s : "SCOPE_" + s)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // username/email not available yet (could be enriched later via auth-service)
        return new CurrentUserResponse(id,
                (username != null && !username.isBlank()) ? username : null,
                (email != null && !email.isBlank()) ? email : null,
                roles == null ? List.of() : roles.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isBlank()).distinct().sorted().toList(),
                permissions);
    }
}
