package com.example.auth.domain.account;

public interface PasswordHasher {
    boolean matches(CharSequence raw, String hash);
    String encode(CharSequence raw);
}
