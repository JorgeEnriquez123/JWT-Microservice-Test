package com.jorge.userservice.exceptions.jwt;

public class TokenMalformedException extends RuntimeException{
    public TokenMalformedException() {
    }

    public TokenMalformedException(String message) {
        super(message);
    }

    public TokenMalformedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenMalformedException(Throwable cause) {
        super(cause);
    }

    public TokenMalformedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
