package com.jorge.userservice.handlers;

import com.jorge.userservice.exceptions.AuthException;
import com.jorge.userservice.exceptions.JwtValidationException;
import com.jorge.userservice.model.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlers {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ExceptionResponse internalServerError(RuntimeException ex){
        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse authHandler(AuthException ex){
        return buildExceptionResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(JwtValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse jwtValidationHandler(JwtValidationException ex){
        return buildExceptionResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    public ExceptionResponse buildExceptionResponse(HttpStatus httpStatus, String exceptionMessage){
        return ExceptionResponse.builder()
                .status(httpStatus.value())
                .message(exceptionMessage)
                .build();
    }
}

