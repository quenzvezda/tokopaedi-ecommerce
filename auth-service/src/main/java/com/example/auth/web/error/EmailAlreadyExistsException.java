package com.example.auth.web.error;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() { super("email_taken"); }
}
