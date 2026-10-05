package com.senhadeguess.backend.persistence.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class PlayerRecord {
    @Id
    @Column(name = "player_id", nullable = false, length = 128)
    private String playerId;

    protected PlayerRecord() { }

    public PlayerRecord(String playerId) { this.playerId = playerId; }
    public String getPlayerId() { return playerId; }
}