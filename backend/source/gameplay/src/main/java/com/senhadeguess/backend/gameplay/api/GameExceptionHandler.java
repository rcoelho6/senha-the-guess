package com.senhadeguess.backend.gameplay.api;

import com.senhadeguess.backend.gameplay.api.dto.ApiError;
import com.senhadeguess.backend.gameplay.domain.GameException;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GameExceptionHandler {
    @ExceptionHandler(GameException.class)
    ResponseEntity<ApiError> handleGameException(GameException exception) {
        return ResponseEntity.status(exception.status()).body(new ApiError(Instant.now(), exception.status().value(), exception.code(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst().map(error -> error.getField() + " " + error.getDefaultMessage()).orElse("Requisição inválida.");
        return ResponseEntity.badRequest().body(new ApiError(Instant.now(), 400, "VALIDATION_ERROR", message));
    }
}