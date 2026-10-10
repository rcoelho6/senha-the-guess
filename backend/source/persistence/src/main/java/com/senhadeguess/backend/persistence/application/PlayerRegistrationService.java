package com.senhadeguess.backend.persistence.application;

import com.senhadeguess.backend.persistence.domain.PlayerRecord;
import com.senhadeguess.backend.persistence.infrastructure.postgres.PlayerRepository;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class PlayerRegistrationService {
    private final PlayerRepository playerRepository;

    public PlayerRegistrationService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public PlayerRecord register(String playerId, String email) {
        String normalizedEmail = PlayerRecord.normalizeEmail(email);
        var existing = playerRepository.findByEmail(normalizedEmail);
        if (existing.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT, "Player ID already exists");
        }

        PlayerRecord player = new PlayerRecord(
                nonNull(playerId) ? playerId : UUID.randomUUID().toString(),
                normalizedEmail
        );

        return playerRepository.save(player);
    }
}
