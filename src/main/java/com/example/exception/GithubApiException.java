package com.example.exception;

public class GithubApiException extends RuntimeException {
    
    public GithubApiException(String message) {
        super(message);
    }
    
    public GithubApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
