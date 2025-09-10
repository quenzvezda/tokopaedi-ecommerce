package com.example.auth.web;

import com.example.auth.application.account.AccountQueries;
import com.example.auth_service.web.api.UserApi;
import com.example.auth_service.web.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final AccountQueries accountQueries;

    @Override
    public ResponseEntity<List<User>> listUsers() {
        List<User> body = accountQueries.list().stream()
                .map(a -> new User().id(a.getId()).username(a.getUsername()))
                .toList();
        return ResponseEntity.ok(body);
    }
}
