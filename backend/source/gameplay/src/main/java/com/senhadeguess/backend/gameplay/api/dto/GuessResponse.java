package com.senhadeguess.backend.gameplay.api.dto;

import com.senhadeguess.backend.gameplay.domain.GameStatus;
import com.senhadeguess.backend.gameplay.domain.GuessResult;
import java.time.Instant;

public record GuessResponse(String gameplayId, String playerId, String guess, GuessResult result, GameStatus status, Instant createdAt) {
}