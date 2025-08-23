package com.example.iam.web;

import com.example.iam.application.user.UserQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
public class UserQueryController {
    private final UserQueries queries;

    @GetMapping("/{accountId}/roles")
    public List<String> getRoles(@PathVariable UUID accountId) {
        return queries.getUserRoleNames(accountId);
    }
}
