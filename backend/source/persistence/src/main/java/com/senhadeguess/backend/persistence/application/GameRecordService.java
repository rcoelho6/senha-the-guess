package com.senhadeguess.backend.persistence.application;

import com.senhadeguess.backend.persistence.api.dto.GameRecordRequest;
import com.senhadeguess.backend.persistence.domain.PersistedGameRecord;
import com.senhadeguess.backend.persistence.infrastructure.postgres.GameRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class GameRecordService {
    private static final Logger log = LoggerFactory.getLogger(GameRecordService.class);
    private final GameRecordRepository repository;
    private final ObjectMapper json;

    public GameRecordService(GameRecordRepository repository, ObjectMapper json) {
        this.repository = repository;
        this.json = json;
    }

    @Async
    public void storeAsync(GameRecordRequest request) {
        try {
            String state = json.writeValueAsString(request.state());
            repository.save(new PersistedGameRecord(request.gameplayId(), request.gameplayTimestamp(), request.recordType(), state));
        } catch (RuntimeException exception) {
            // MVP sem retry/outbox: registra somente o ID, nunca payloads potencialmente sensíveis.
            log.error("Falha ao gravar atualização histórica gameplayId={} type={}", request.gameplayId(), request.recordType(), exception);
        }
    }
}