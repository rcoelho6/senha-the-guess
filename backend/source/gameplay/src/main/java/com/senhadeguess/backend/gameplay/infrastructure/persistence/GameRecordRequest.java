package com.senhadeguess.backend.gameplay.infrastructure.persistence;

import java.time.Instant;
import java.util.Map;

public record GameRecordRequest(String gameplayId, Instant gameplayTimestamp, String recordType, Map<String, Object> state) {
}