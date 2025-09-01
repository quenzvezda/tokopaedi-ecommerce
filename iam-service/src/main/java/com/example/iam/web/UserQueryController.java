package com.example.iam.web;

import com.example.iam.application.user.UserQueries;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
@Tag(name = "6. User")
public class UserQueryController {
    private final UserQueries queries;

    @GetMapping("/{accountId}/roles")
    @Operation(operationId = "user_1_get_roles", summary = "Get user roles", security = {@SecurityRequirement(name = "bearer-key")})
    public List<String> getRoles(@PathVariable UUID accountId) {
        return queries.getUserRoleNames(accountId);
    }
}
