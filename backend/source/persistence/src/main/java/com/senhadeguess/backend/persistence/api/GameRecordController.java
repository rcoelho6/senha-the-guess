package com.senhadeguess.backend.persistence.api;

import com.senhadeguess.backend.persistence.api.dto.GameRecordAcceptedResponse;
import com.senhadeguess.backend.persistence.api.dto.GameRecordRequest;
import com.senhadeguess.backend.persistence.application.GameRecordService;
import jakarta.validation.Valid;
import java.time.Clock;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/game-records")
public class GameRecordController {
    private final GameRecordService service;
    private final Clock clock;

    public GameRecordController(GameRecordService service, Clock clock) {
        this.service = service;
        this.clock = clock;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GameRecordAcceptedResponse accept(@Valid @RequestBody GameRecordRequest request) {
        service.storeAsync(request);
        return new GameRecordAcceptedResponse(request.gameplayId(), true, clock.instant());
    }
}