package com.senhadeguess.backend.persistence.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.senhadeguess.backend.persistence.infrastructure.postgres.PlayerRepository;
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
}