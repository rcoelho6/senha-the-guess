package com.senhadeguess.backend.persistence.api;

import com.senhadeguess.backend.persistence.api.dto.PlayerRegistrationRequest;
import com.senhadeguess.backend.persistence.api.dto.PlayerRegistrationResponse;
import com.senhadeguess.backend.persistence.application.PlayerRegistrationService;
import com.senhadeguess.backend.persistence.domain.PlayerRecord;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/players")
public class PlayerController {
    private final PlayerRegistrationService service;

    public PlayerController(PlayerRegistrationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PlayerRegistrationResponse> register(@Valid @RequestBody PlayerRegistrationRequest request) {
        PlayerRecord result = service.register(request.playerId(), request.email());
        PlayerRegistrationResponse response = new PlayerRegistrationResponse(
                result.getPlayerId(),
                result.getEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
