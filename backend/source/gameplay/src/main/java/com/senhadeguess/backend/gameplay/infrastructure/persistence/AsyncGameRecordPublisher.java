package com.senhadeguess.backend.gameplay.infrastructure.persistence;

import com.senhadeguess.backend.gameplay.application.GameRecordPublisher;
import com.senhadeguess.backend.gameplay.domain.GameRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncGameRecordPublisher implements GameRecordPublisher {
    private static final Logger log = LoggerFactory.getLogger(AsyncGameRecordPublisher.class);
    private final PersistenceClient persistenceClient;

    public AsyncGameRecordPublisher(PersistenceClient persistenceClient) {
        this.persistenceClient = persistenceClient;
    }

    @Override
    @Async
    public void publish(GameRecord record) {
        try {
            persistenceClient.submitGameRecord(record);
        } catch (RuntimeException exception) {
            log.warn("Não foi possível enviar atualização histórica para gameplayId={} (sem retry na MVP).", record.gameplayId());
        }
    }
}