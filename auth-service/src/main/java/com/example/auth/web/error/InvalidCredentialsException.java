package com.example.auth.web.error;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() { super("invalid_credentials"); }
}
