package com.senhadeguess.backend.persistence.api.dto;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String message) {
}