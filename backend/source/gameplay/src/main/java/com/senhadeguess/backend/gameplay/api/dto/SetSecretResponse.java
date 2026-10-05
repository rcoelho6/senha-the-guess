package com.senhadeguess.backend.gameplay.api.dto;

import com.senhadeguess.backend.gameplay.domain.GameStatus;

public record SetSecretResponse(String gameplayId, GameStatus status, String playerId, boolean ownSecretSet, boolean bothSecretsSet) {
}