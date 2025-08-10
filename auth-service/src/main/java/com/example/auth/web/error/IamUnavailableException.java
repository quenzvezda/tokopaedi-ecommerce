package com.example.auth.web.error;

public class IamUnavailableException extends RuntimeException {
    public IamUnavailableException(String reason) { super("iam_unavailable:" + reason); }
}
