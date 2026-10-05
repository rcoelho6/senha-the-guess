package com.senhadeguess.backend.gameplay.infrastructure.persistence;

public record PlayerValidationResponse(String playerId, boolean exists) {
}