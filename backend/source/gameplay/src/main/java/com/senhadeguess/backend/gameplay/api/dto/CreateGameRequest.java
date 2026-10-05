package com.senhadeguess.backend.gameplay.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGameRequest(
        @NotBlank @Size(max = 128) String playerId,
        @NotBlank @Size(max = 128) String opponentPlayerId) {
}