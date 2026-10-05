package com.senhadeguess.backend.gameplay.domain;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record GameRecord(String gameplayId, Instant gameplayTimestamp, RecordType recordType, Map<String, Object> state) {
    public GameRecord {
        state = Collections.unmodifiableMap(new LinkedHashMap<>(state));
    }
}