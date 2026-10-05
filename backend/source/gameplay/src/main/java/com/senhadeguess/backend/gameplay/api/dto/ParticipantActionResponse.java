package com.senhadeguess.backend.gameplay.api.dto;

import com.senhadeguess.backend.gameplay.domain.GameStatus;
import java.time.Instant;

public record ParticipantActionResponse(String gameplayId, GameStatus status, String playerId, Instant updatedAt) {
}