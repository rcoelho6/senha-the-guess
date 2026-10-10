package com.senhadeguess.backend.persistence.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.senhadeguess.backend.persistence.domain.PlayerRecord;
import com.senhadeguess.backend.persistence.infrastructure.postgres.PlayerRepository;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class PlayerRegistrationServiceTest {
    @Test
    void registersNormalizedEmailOnce() {
        PlayerRepository repository = mock(PlayerRepository.class);
        AtomicReference<PlayerRecord> stored = new AtomicReference<>();
        when(repository.findByEmail("player@example.com"))
                .thenAnswer(invocation -> Optional.ofNullable(stored.get()));
        when(repository.insertIfAbsent(anyString(), eq("player@example.com")))
                .thenAnswer(invocation -> {
                    stored.set(new PlayerRecord(invocation.getArgument(0), invocation.getArgument(1)));
                    return 1;
                });
        PlayerRegistrationService service = new PlayerRegistrationService(repository);

        PlayerRegistrationService.RegistrationResult first = service.register(" Player@Example.com ");
        PlayerRegistrationService.RegistrationResult second = service.register("player@example.com");

        assertTrue(first.created());
        assertFalse(second.created());
        assertEquals(first.player().getPlayerId(), second.player().getPlayerId());
        assertEquals("player@example.com", first.player().getEmail());
        verify(repository).insertIfAbsent(anyString(), eq("player@example.com"));
    }

    @Test
    void returnsExistingPlayerWhenConcurrentInsertWinsElsewhere() {
        PlayerRepository repository = mock(PlayerRepository.class);
        PlayerRecord existing = new PlayerRecord("player-existing", "player@example.com");
        when(repository.findByEmail("player@example.com")).thenReturn(Optional.empty(), Optional.of(existing));
        when(repository.insertIfAbsent(anyString(), eq("player@example.com"))).thenReturn(0);
        PlayerRegistrationService service = new PlayerRegistrationService(repository);

        PlayerRegistrationService.RegistrationResult result = service.register("player@example.com");

        assertFalse(result.created());
        assertEquals("player-existing", result.player().getPlayerId());
    }
}
