package com.jorge.userservice.handlers;

import com.jorge.userservice.exceptions.AuthException;
import com.jorge.userservice.model.dto.LoginErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlers {
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public LoginErrorResponse authHandler(AuthException ex){
        return LoginErrorResponse.builder()
                .status(401)
                .message(ex.getMessage())
                .build();
    }
}
