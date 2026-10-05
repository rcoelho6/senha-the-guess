package com.senhadeguess.backend.gameplay.infrastructure.redis;

import com.senhadeguess.backend.gameplay.domain.Game;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
public class RedisGameStateRepository {
    private static final int MAX_TRANSACTION_ATTEMPTS = 20;
    private final RedisTemplate<String, String> redis;
    private final ObjectMapper json;

    public RedisGameStateRepository(RedisTemplate<String, String> redis, ObjectMapper json) {
        this.redis = redis;
        this.json = json;
    }

    public Optional<Game> find(String gameplayId) {
        String value = redis.opsForValue().get(gameKey(gameplayId));
        return value == null ? Optional.empty() : Optional.of(readGame(value));
    }

    public <T> T mutate(String gameplayId, BiFunction<Game, Map<String, String>, StateMutation<T>> action) {
        String gameKey = gameKey(gameplayId);
        String presenceKey = presenceKey(gameplayId);
        for (int attempt = 0; attempt < MAX_TRANSACTION_ATTEMPTS; attempt++) {
            Attempt<T> committed = redis.execute(new SessionCallback<Attempt<T>>() {
                @Override
                @SuppressWarnings({"unchecked", "rawtypes"})
                public <K, V> Attempt<T> execute(RedisOperations<K, V> session) throws DataAccessException {
                    RedisOperations<String, String> ops = (RedisOperations<String, String>) (RedisOperations) session;
                    ops.watch(gameKey, presenceKey);
                    try {
                        String gameJson = ops.opsForValue().get(gameKey);
                        String presenceJson = ops.opsForValue().get(presenceKey);
                        Game current = gameJson == null ? null : readGame(gameJson);
                        Map<String, String> presence = readPresence(presenceJson);
                        StateMutation<T> next = action.apply(current, presence);
                        ops.multi();
                        ops.opsForValue().set(gameKey, writeJson(next.game()));
                        ops.opsForValue().set(presenceKey, writeJson(next.presence()));
                        return ops.exec() == null ? null : new Attempt<>(next.result());
                    } catch (RuntimeException exception) {
                        try { ops.unwatch(); } catch (RuntimeException ignored) { }
                        throw exception;
                    }
                }
            });
            if (committed != null) return committed.result();
        }
        throw new IllegalStateException("A partida sofreu muitas atualizações concorrentes; tente novamente.");
    }

    private Game readGame(String value) {
        return json.readValue(value, Game.class);
    }

    private Map<String, String> readPresence(String value) {
        if (value == null || value.isBlank()) return new HashMap<>();
        Map<?, ?> decoded = json.readValue(value, Map.class);
        Map<String, String> result = new HashMap<>();
        decoded.forEach((key, timestamp) -> result.put(String.valueOf(key), String.valueOf(timestamp)));
        return result;
    }

    private String writeJson(Object value) {
        return json.writeValueAsString(value);
    }

    private String gameKey(String gameplayId) {
        return "{" + gameplayId + "}:game";
    }

    private String presenceKey(String gameplayId) {
        return "{" + gameplayId + "}:presence";
    }

    public record StateMutation<T>(Game game, Map<String, String> presence, T result) {
        public StateMutation {
            presence = Map.copyOf(presence);
        }
    }

    private record Attempt<T>(T result) { }
}