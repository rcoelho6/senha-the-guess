package com.senhadeguess.backend.gameplay.api.dto;

import com.senhadeguess.backend.gameplay.domain.FinishReason;
import com.senhadeguess.backend.gameplay.domain.GameStatus;
import java.time.Instant;

public record HeartbeatResponse(String gameplayId, String playerId, Instant updatedAt, GameStatus status, FinishReason finishReason) {
}