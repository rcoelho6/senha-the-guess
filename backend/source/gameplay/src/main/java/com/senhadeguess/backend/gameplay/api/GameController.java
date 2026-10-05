package com.senhadeguess.backend.gameplay.api;

import com.senhadeguess.backend.gameplay.api.dto.CreateGameRequest;
import com.senhadeguess.backend.gameplay.api.dto.GameCreatedResponse;
import com.senhadeguess.backend.gameplay.api.dto.GameStateResponse;
import com.senhadeguess.backend.gameplay.api.dto.GuessRequest;
import com.senhadeguess.backend.gameplay.api.dto.GuessResponse;
import com.senhadeguess.backend.gameplay.api.dto.HeartbeatResponse;
import com.senhadeguess.backend.gameplay.api.dto.ParticipantActionResponse;
import com.senhadeguess.backend.gameplay.api.dto.SetSecretRequest;
import com.senhadeguess.backend.gameplay.api.dto.SetSecretResponse;
import com.senhadeguess.backend.gameplay.application.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService service;

    public GameController(GameService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameCreatedResponse create(@Valid @RequestBody CreateGameRequest request) {
        return service.create(request.playerId(), request.opponentPlayerId());
    }

    @PatchMapping("/{gameplayId}/players/{playerId}/join")
    public ParticipantActionResponse join(@PathVariable String gameplayId, @PathVariable String playerId) {
        return service.join(gameplayId, playerId);
    }

    @PatchMapping("/{gameplayId}/players/{playerId}/decline")
    public ParticipantActionResponse decline(@PathVariable String gameplayId, @PathVariable String playerId) {
        return service.decline(gameplayId, playerId);
    }

    @PutMapping("/{gameplayId}/players/{playerId}/secret")
    public SetSecretResponse setSecret(@PathVariable String gameplayId, @PathVariable String playerId,
                                       @Valid @RequestBody SetSecretRequest request) {
        return service.setSecret(gameplayId, playerId, request.digits());
    }

    @PatchMapping("/{gameplayId}/players/{playerId}/heartbeat")
    public HeartbeatResponse heartbeat(@PathVariable String gameplayId, @PathVariable String playerId) {
        return service.heartbeat(gameplayId, playerId);
    }

    @PutMapping("/{gameplayId}/players/{playerId}/guess")
    public GuessResponse guess(@PathVariable String gameplayId, @PathVariable String playerId,
                               @Valid @RequestBody GuessRequest request) {
        return service.guess(gameplayId, playerId, request.digits());
    }

    @GetMapping("/{gameplayId}/state")
    public GameStateResponse state(@PathVariable String gameplayId) {
        return service.getState(gameplayId);
    }
}