package com.jorge.gateway.exception;

public class UserServiceNotAvailableException extends RuntimeException{
    public UserServiceNotAvailableException() {
    }

    public UserServiceNotAvailableException(String message) {
        super(message);
    }

    public UserServiceNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserServiceNotAvailableException(Throwable cause) {
        super(cause);
    }

    public UserServiceNotAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
