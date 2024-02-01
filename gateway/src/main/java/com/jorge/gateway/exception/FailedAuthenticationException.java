package com.jorge.gateway.exception;

public class FailedAuthenticationException extends RuntimeException{
    public FailedAuthenticationException() {
    }

    public FailedAuthenticationException(String message) {
        super(message);
    }

    public FailedAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedAuthenticationException(Throwable cause) {
        super(cause);
    }

    public FailedAuthenticationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
