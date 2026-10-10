package com.senhadeguess.backend.persistence.application;

import com.senhadeguess.backend.persistence.infrastructure.postgres.PlayerRepository;
import com.senhadeguess.backend.persistence.domain.PlayerRecord;
import java.util.Optional;
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

    public Optional<String> findPlayerIdByEmail(String email) {
        return playerRepository.findByEmail(PlayerRecord.normalizeEmail(email))
                .map(PlayerRecord::getPlayerId);
    }
}
