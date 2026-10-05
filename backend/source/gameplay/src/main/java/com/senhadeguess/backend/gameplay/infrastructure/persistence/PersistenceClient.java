package com.senhadeguess.backend.gameplay.infrastructure.persistence;

import com.senhadeguess.backend.gameplay.domain.GameException;
import com.senhadeguess.backend.gameplay.domain.GameRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class PersistenceClient {
    private final RestClient client;

    public PersistenceClient(RestClient.Builder builder, @Value("${services.persistence.base-url:http://localhost:8081}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    public void assertPlayerExists(String playerId) {
        try {
            PlayerValidationResponse response = client.get()
                    .uri("/internal/v1/players/{playerId}", playerId)
                    .retrieve()
                    .body(PlayerValidationResponse.class);
            if (response == null || !response.exists()) {
                throw new GameException("PLAYER_NOT_FOUND", "O jogador informado não está cadastrado.", HttpStatus.BAD_REQUEST);
            }
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new GameException("PLAYER_NOT_FOUND", "O jogador informado não está cadastrado.", HttpStatus.BAD_REQUEST);
            }
            throw unavailable(exception);
        } catch (RestClientException exception) {
            throw unavailable(exception);
        }
    }

    public void submitGameRecord(GameRecord record) {
        try {
            client.post().uri("/internal/v1/game-records")
                    .body(new GameRecordRequest(record.gameplayId(), record.gameplayTimestamp(), record.recordType().name(), record.state()))
                    .retrieve().toBodilessEntity();
        } catch (RestClientException exception) {
            throw unavailable(exception);
        }
    }

    private GameException unavailable(Exception cause) {
        return new GameException("PERSISTENCE_UNAVAILABLE", "O serviço de Persistência está indisponível.", HttpStatus.SERVICE_UNAVAILABLE, cause);
    }
}