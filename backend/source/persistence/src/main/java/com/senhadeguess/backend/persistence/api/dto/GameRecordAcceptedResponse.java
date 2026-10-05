package com.senhadeguess.backend.persistence.api.dto;

import java.time.Instant;

public record GameRecordAcceptedResponse(String gameplayId, boolean accepted, Instant receivedAt) {
}