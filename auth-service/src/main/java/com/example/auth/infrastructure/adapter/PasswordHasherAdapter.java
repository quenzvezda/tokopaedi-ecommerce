package com.example.auth.infrastructure.adapter;

import com.example.auth.domain.port.PasswordHasherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class PasswordHasherAdapter implements PasswordHasherPort {
    private final PasswordEncoder encoder;
    @Override public boolean matches(CharSequence raw, String hash) { return encoder.matches(raw, hash); }
    @Override public String encode(CharSequence raw) { return encoder.encode(raw); }
}
