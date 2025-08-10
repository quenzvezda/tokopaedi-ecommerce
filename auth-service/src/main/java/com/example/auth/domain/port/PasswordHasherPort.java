package com.example.auth.domain.port;

public interface PasswordHasherPort {
    boolean matches(CharSequence raw, String hash);
    String encode(CharSequence raw);
}
