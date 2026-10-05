package com.senhadeguess.backend.persistence.application;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.senhadeguess.backend.persistence.api.dto.GameRecordRequest;
import com.senhadeguess.backend.persistence.infrastructure.postgres.GameRecordRepository;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class GameRecordServiceTest {
    @Test
    void serializesSnapshotAndAppendsRecord() {
        GameRecordRepository repository = mock(GameRecordRepository.class);
        GameRecordService service = new GameRecordService(repository, JsonMapper.builder().build());
        Instant timestamp = Instant.parse("2026-10-05T15:03:00Z");
        GameRecordRequest request = new GameRecordRequest("game-789", timestamp, "GUESS_RECORDED",
                Map.of("status", "ACTIVE", "playerIds", java.util.List.of("player-123", "player-456")));

        service.storeAsync(request);

        verify(repository).save(argThat(record -> record.getGameplayId().equals("game-789")
                && record.getGameplayTimestamp().equals(timestamp)
                && record.getRecordType().equals("GUESS_RECORDED")
                && record.getStateJson().contains("ACTIVE")));
    }
}