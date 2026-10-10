package com.senhadeguess.backend.persistence.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Locale;

@Entity
@Table(name = "players")
public class PlayerRecord {
    @Id
    @Column(name = "player_id", nullable = false, length = 128)
    private String playerId;

    @Column(name = "email", length = 254, unique = true)
    private String email;

    protected PlayerRecord() { }

    public PlayerRecord(String playerId, String email) {
        this.playerId = playerId;
        this.email = normalizeEmail(email);
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public String getPlayerId() { return playerId; }
    public String getEmail() { return email; }
}
