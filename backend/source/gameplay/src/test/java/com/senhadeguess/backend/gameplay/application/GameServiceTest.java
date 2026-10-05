package com.senhadeguess.backend.gameplay.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.senhadeguess.backend.gameplay.domain.GameException;
import com.senhadeguess.backend.gameplay.infrastructure.persistence.PersistenceClient;
import com.senhadeguess.backend.gameplay.infrastructure.redis.RedisGameStateRepository;
import java.time.Clock;
import org.junit.jupiter.api.Test;

class GameServiceTest {
    @Test
    void refusesToCreateGameWithSamePlayerTwiceBeforeCallingDependencies() {
        RedisGameStateRepository repository = mock(RedisGameStateRepository.class);
        PersistenceClient client = mock(PersistenceClient.class);
        GameRecordPublisher publisher = mock(GameRecordPublisher.class);
        GameService service = new GameService(repository, client, publisher, new GuessEvaluator(),
                new PresenceTimeoutService(), Clock.systemUTC());

        assertThrows(GameException.class, () -> service.create("player-1", "player-1"));
        verifyNoInteractions(repository, client, publisher);
    }
}