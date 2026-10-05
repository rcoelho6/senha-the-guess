package com.senhadeguess.backend.gameplay.domain;

import org.springframework.http.HttpStatus;

public class GameException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public GameException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public GameException(String code, String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }

    public String code() { return code; }
    public HttpStatus status() { return status; }
}