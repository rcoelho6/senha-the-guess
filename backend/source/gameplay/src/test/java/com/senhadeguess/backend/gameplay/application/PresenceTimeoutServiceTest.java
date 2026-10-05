package com.senhadeguess.backend.gameplay.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.senhadeguess.backend.gameplay.domain.Game;
import com.senhadeguess.backend.gameplay.domain.GameStatus;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PresenceTimeoutServiceTest {
    private final PresenceTimeoutService service = new PresenceTimeoutService();
    private final Instant activation = Instant.parse("2026-10-05T12:00:00Z");
    private final Game game = new Game("game-1", "a", "b", GameStatus.ACTIVE, "1234", "5678", true,
            List.of(), activation, activation, activation, null);

    @Test
    void exactlyFiveSecondsIsNotATimeout() {
        assertFalse(service.hasTimedOut(game, Map.of("a", activation.toString(), "b", activation.toString()), activation.plusSeconds(5)));
    }

    @Test
    void moreThanFiveSecondsIsATimeout() {
        assertTrue(service.hasTimedOut(game, Map.of("a", activation.toString(), "b", activation.toString()), activation.plusMillis(5001)));
    }

    @Test
    void missingFirstHeartbeatUsesActivationTime() {
        assertTrue(service.hasTimedOut(game, Map.of("a", activation.plusSeconds(5).toString()), activation.plusMillis(5001)));
    }
}