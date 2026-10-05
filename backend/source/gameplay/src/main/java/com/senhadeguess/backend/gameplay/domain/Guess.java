package com.senhadeguess.backend.gameplay.domain;

import java.time.Instant;

public record Guess(String playerId, String digits, GuessResult result, Instant createdAt) {
}