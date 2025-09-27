package com.example.auth.application.account;

import java.util.UUID;

public interface AccountCommands {

    /**
     * Register akun baru beserta metadata awal untuk provisioning lintas layanan.
     */
    UUID register(String username, String email, String rawPassword, String fullName, String phone);
}
