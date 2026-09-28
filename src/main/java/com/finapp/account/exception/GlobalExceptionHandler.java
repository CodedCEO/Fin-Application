package com.finapp.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FinAppValidationException.class)
    public ResponseEntity<?> handleCardValidationException(FinAppValidationException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(Map.of("status", ex.getStatus().value(), "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleRequestBodyValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "message", message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = ex.getRequiredType() == null
                ? ex.getName() + " has an invalid value"
                : ex.getName() + " must be of type " + ex.getRequiredType().getSimpleName();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "message", message));
    }
}
