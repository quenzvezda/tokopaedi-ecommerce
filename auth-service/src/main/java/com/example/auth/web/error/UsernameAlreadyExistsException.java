package com.example.auth.web.error;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() { super("username_taken"); }
}
