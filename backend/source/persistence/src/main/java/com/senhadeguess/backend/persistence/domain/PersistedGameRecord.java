package com.senhadeguess.backend.persistence.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "game_records", indexes = @Index(name = "idx_game_records_game_time", columnList = "gameplay_id, gameplay_timestamp"))
public class PersistedGameRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gameplay_id", nullable = false, length = 128)
    private String gameplayId;

    @Column(name = "gameplay_timestamp", nullable = false)
    private Instant gameplayTimestamp;

    @Column(name = "record_type", nullable = false, length = 64)
    private String recordType;

    @Column(name = "state_json", nullable = false, columnDefinition = "text")
    private String stateJson;

    protected PersistedGameRecord() { }

    public PersistedGameRecord(String gameplayId, Instant gameplayTimestamp, String recordType, String stateJson) {
        this.gameplayId = gameplayId;
        this.gameplayTimestamp = gameplayTimestamp;
        this.recordType = recordType;
        this.stateJson = stateJson;
    }

    public Long getId() { return id; }
    public String getGameplayId() { return gameplayId; }
    public Instant getGameplayTimestamp() { return gameplayTimestamp; }
    public String getRecordType() { return recordType; }
    public String getStateJson() { return stateJson; }
}