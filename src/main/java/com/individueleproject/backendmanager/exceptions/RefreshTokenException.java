package com.individueleproject.backendmanager.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RefreshTokenException extends RuntimeException{
    public RefreshTokenException(String message) {
        super(String.format(message));
    }
}
