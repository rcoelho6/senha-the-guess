package com.senhadeguess.backend.persistence.api;

import com.senhadeguess.backend.persistence.api.dto.PlayerValidationResponse;
import com.senhadeguess.backend.persistence.application.PlayerValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/v1/players")
public class InternalPlayerController {
    private final PlayerValidationService service;

    public InternalPlayerController(PlayerValidationService service) { this.service = service; }

    @GetMapping("/{playerId}")
    public PlayerValidationResponse validate(@PathVariable String playerId) {
        if (!service.exists(playerId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        return new PlayerValidationResponse(playerId, true);
    }

    @GetMapping("/lookup")
    public PlayerValidationResponse findByEmail(@RequestParam String email) {
        if (email.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        String playerId = service.findPlayerIdByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
        return new PlayerValidationResponse(playerId, true);
    }
}
