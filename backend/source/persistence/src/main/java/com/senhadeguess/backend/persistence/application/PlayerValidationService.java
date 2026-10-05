package com.senhadeguess.backend.persistence.application;

import com.senhadeguess.backend.persistence.infrastructure.postgres.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerValidationService {
    private final PlayerRepository playerRepository;

    public PlayerValidationService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public boolean exists(String playerId) {
        return playerRepository.existsById(playerId);
    }
}