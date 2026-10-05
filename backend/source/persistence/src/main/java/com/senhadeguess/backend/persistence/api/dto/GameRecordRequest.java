package com.senhadeguess.backend.persistence.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.Map;

public record GameRecordRequest(
        @NotBlank @Size(max = 128) String gameplayId,
        @NotNull Instant gameplayTimestamp,
        @NotBlank @Size(max = 64) @Pattern(regexp = "GAME_CREATED|PLAYER_JOINED|GAME_DECLINED|SECRET_SET|GUESS_RECORDED|GAME_FINISHED") String recordType,
        @NotNull Map<String, Object> state) {
}