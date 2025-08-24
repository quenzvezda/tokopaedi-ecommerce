package com.example.auth.application.account;

import java.util.UUID;

public interface AccountCommands {

    /**
     * Register akun baru.
     */
    UUID register(String username, String email, String rawPassword);
}
