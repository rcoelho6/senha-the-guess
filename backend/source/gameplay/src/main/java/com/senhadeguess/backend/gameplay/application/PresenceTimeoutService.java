package com.senhadeguess.backend.gameplay.application;

import com.senhadeguess.backend.gameplay.domain.Game;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PresenceTimeoutService {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    public boolean hasTimedOut(Game game, Map<String, String> lastHeartbeats, Instant now) {
        if (game.activatedAt() == null) return false;
        return timedOut(game.creatorPlayerId(), game, lastHeartbeats, now)
                || timedOut(game.opponentPlayerId(), game, lastHeartbeats, now);
    }

    private boolean timedOut(String playerId, Game game, Map<String, String> lastHeartbeats, Instant now) {
        String value = lastHeartbeats.get(playerId);
        Instant lastHeartbeat = value == null ? game.activatedAt() : Instant.parse(value);
        return Duration.between(lastHeartbeat, now).compareTo(TIMEOUT) > 0;
    }
}