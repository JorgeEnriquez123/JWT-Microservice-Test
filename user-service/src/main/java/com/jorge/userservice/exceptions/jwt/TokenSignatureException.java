package com.jorge.userservice.exceptions.jwt;

public class TokenSignatureException extends RuntimeException{
    public TokenSignatureException() {
    }

    public TokenSignatureException(String message) {
        super(message);
    }

    public TokenSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenSignatureException(Throwable cause) {
        super(cause);
    }

    public TokenSignatureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
