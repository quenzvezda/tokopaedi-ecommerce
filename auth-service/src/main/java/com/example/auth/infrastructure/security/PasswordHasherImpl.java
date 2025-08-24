package com.example.auth.infrastructure.security;

import com.example.auth.domain.account.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password hasher adapter → di slice security.
 */
public class PasswordHasherImpl implements PasswordHasher {
    private final PasswordEncoder encoder;

    public PasswordHasherImpl(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public boolean matches(CharSequence raw, String hash) {
        return encoder.matches(raw, hash);
    }

    @Override
    public String encode(CharSequence raw) {
        return encoder.encode(raw);
    }
}
