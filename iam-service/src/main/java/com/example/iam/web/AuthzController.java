package com.example.iam.web;

import com.example.iam.application.query.CheckAuthorizationQuery;
import com.example.iam.web.dto.AuthzCheckRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/authz")
@RequiredArgsConstructor
public class AuthzController {

    private final CheckAuthorizationQuery check;

    @PostMapping("/check")
    public Map<String, Object> check(@Valid @RequestBody AuthzCheckRequest req) {
        return check.handle(req.getSub(), req.getAction());
    }
}
