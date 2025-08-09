package com.example.iam.web;

import com.example.iam.application.query.GetUserRolesQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserQueryController {
    private final GetUserRolesQuery rolesQuery;

    @GetMapping("/{accountId}/roles")
    public List<String> getRoles(@PathVariable("accountId") UUID accountId) { return rolesQuery.handle(accountId); }
}
