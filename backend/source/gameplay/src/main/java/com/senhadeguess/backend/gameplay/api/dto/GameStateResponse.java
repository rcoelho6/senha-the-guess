package com.senhadeguess.backend.gameplay.api.dto;

import com.senhadeguess.backend.gameplay.domain.FinishReason;
import com.senhadeguess.backend.gameplay.domain.GameStatus;
import com.senhadeguess.backend.gameplay.domain.Guess;
import java.util.List;
import java.util.Map;

public record GameStateResponse(
        String gameplayId,
        GameStatus status,
        List<String> playerIds,
        Map<String, Boolean> secretsSet,
        List<Guess> guesses,
        FinishReason finishReason) {
}