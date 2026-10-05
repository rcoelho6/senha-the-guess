package com.senhadeguess.backend.gameplay.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record Game(
        String gameplayId,
        String creatorPlayerId,
        String opponentPlayerId,
        GameStatus status,
        String creatorSecret,
        String opponentSecret,
        boolean opponentJoined,
        List<Guess> guesses,
        Instant createdAt,
        Instant updatedAt,
        Instant activatedAt,
        FinishReason finishReason) {

    public Game {
        Objects.requireNonNull(gameplayId);
        Objects.requireNonNull(creatorPlayerId);
        Objects.requireNonNull(opponentPlayerId);
        Objects.requireNonNull(status);
        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(updatedAt);
        guesses = guesses == null ? List.of() : List.copyOf(guesses);
    }

    public boolean containsPlayer(String playerId) {
        return creatorPlayerId.equals(playerId) || opponentPlayerId.equals(playerId);
    }

    public boolean isOpponent(String playerId) {
        return opponentPlayerId.equals(playerId);
    }

    public String secretFor(String playerId) {
        if (creatorPlayerId.equals(playerId)) return creatorSecret;
        if (opponentPlayerId.equals(playerId)) return opponentSecret;
        return null;
    }

    public String secretOfOpponent(String playerId) {
        if (creatorPlayerId.equals(playerId)) return opponentSecret;
        if (opponentPlayerId.equals(playerId)) return creatorSecret;
        return null;
    }

    public boolean bothSecretsSet() {
        return creatorSecret != null && opponentSecret != null;
    }

    @Override
    public String toString() {
        return "Game[gameplayId=" + gameplayId + ", status=" + status
                + ", creatorPlayerId=" + creatorPlayerId + ", opponentPlayerId=" + opponentPlayerId
                + ", creatorSecretSet=" + (creatorSecret != null)
                + ", opponentSecretSet=" + (opponentSecret != null)
                + ", guessCount=" + guesses.size() + "]";
    }
}