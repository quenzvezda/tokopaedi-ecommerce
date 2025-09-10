package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam.application.user.UserQueries;
import com.example.iam_service.web.api.UserApi;
import com.example.iam_service.web.model.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final EntitlementQueries entitlements;
    private final UserQueries userQueries;

    @Override
    public ResponseEntity<CurrentUser> getCurrentUser() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = null;
        if (authentication instanceof JwtAuthenticationToken j) {
            jwt = j.getToken();
        }
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        UUID id = UUID.fromString(jwt.getSubject());
        String username = jwt.getClaimAsString("username");
        String email = jwt.getClaimAsString("email");

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

        Map<String, Object> ent = entitlements.getEntitlements(id);
        @SuppressWarnings("unchecked")
        List<String> scopes = (List<String>) ent.getOrDefault("scopes", List.of());
        List<String> permissions = (scopes == null ? List.of() : scopes).stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .sorted()
                .toList();

        CurrentUser body = new CurrentUser()
                .id(id.toString())
                .username((username != null && !username.isBlank()) ? username : id.toString())
                .email((email != null && !email.isBlank()) ? email : null)
                .roles(roles == null ? List.of() : roles)
                .permissions(permissions);

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<java.util.List<String>> getUserRoles(java.util.UUID accountId) {
        return ResponseEntity.ok(userQueries.getUserRoleNames(accountId));
    }

    @Override
    public ResponseEntity<java.util.List<String>> getUserRolesPublic(java.util.UUID accountId) {
        return ResponseEntity.ok(userQueries.getUserRoleNames(accountId));
    }
}

