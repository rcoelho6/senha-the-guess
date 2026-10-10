package com.senhadeguess.backend.persistence.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.senhadeguess.backend.persistence.domain.PlayerRecord;
import com.senhadeguess.backend.persistence.infrastructure.postgres.PlayerRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PlayerValidationServiceTest {
    @Test
    void delegatesExistenceCheckToPlayerRepository() {
        PlayerRepository repository = mock(PlayerRepository.class);
        when(repository.existsById("player-123")).thenReturn(true);
        when(repository.existsById("missing")).thenReturn(false);
        PlayerValidationService service = new PlayerValidationService(repository);

        assertTrue(service.exists("player-123"));
        assertFalse(service.exists("missing"));
    }

    @Test
    void findsPlayerIdByNormalizedEmail() {
        PlayerRepository repository = mock(PlayerRepository.class);
        when(repository.findByEmail("player@example.com"))
                .thenReturn(Optional.of(new PlayerRecord("player-123", "player@example.com")));
        PlayerValidationService service = new PlayerValidationService(repository);

        assertEquals(Optional.of("player-123"), service.findPlayerIdByEmail(" Player@Example.com "));
    }

    @Test
    void returnsEmptyWhenEmailIsNotRegistered() {
        PlayerRepository repository = mock(PlayerRepository.class);
        when(repository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        PlayerValidationService service = new PlayerValidationService(repository);

        assertEquals(Optional.empty(), service.findPlayerIdByEmail("missing@example.com"));
    }
}
