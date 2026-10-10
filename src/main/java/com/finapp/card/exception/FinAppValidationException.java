package com.finapp.card.exception;

import org.springframework.http.HttpStatus;

public class FinAppValidationException extends RuntimeException {
    private final HttpStatus status;

    public FinAppValidationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
