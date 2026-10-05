package com.senhadeguess.backend.gameplay.application;

import com.senhadeguess.backend.gameplay.api.dto.GameCreatedResponse;
import com.senhadeguess.backend.gameplay.api.dto.GameStateResponse;
import com.senhadeguess.backend.gameplay.api.dto.GuessResponse;
import com.senhadeguess.backend.gameplay.api.dto.HeartbeatResponse;
import com.senhadeguess.backend.gameplay.api.dto.ParticipantActionResponse;
import com.senhadeguess.backend.gameplay.api.dto.SetSecretResponse;
import com.senhadeguess.backend.gameplay.domain.FinishReason;
import com.senhadeguess.backend.gameplay.domain.Game;
import com.senhadeguess.backend.gameplay.domain.GameException;
import com.senhadeguess.backend.gameplay.domain.GameRecord;
import com.senhadeguess.backend.gameplay.domain.GameStatus;
import com.senhadeguess.backend.gameplay.domain.Guess;
import com.senhadeguess.backend.gameplay.domain.GuessResult;
import com.senhadeguess.backend.gameplay.domain.RecordType;
import com.senhadeguess.backend.gameplay.infrastructure.persistence.PersistenceClient;
import com.senhadeguess.backend.gameplay.infrastructure.redis.RedisGameStateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final RedisGameStateRepository repository;
    private final PersistenceClient persistenceClient;
    private final GameRecordPublisher recordPublisher;
    private final GuessEvaluator evaluator;
    private final PresenceTimeoutService timeoutService;
    private final Clock clock;

    public GameService(RedisGameStateRepository repository, PersistenceClient persistenceClient,
                       GameRecordPublisher recordPublisher, GuessEvaluator evaluator,
                       PresenceTimeoutService timeoutService, Clock clock) {
        this.repository = repository;
        this.persistenceClient = persistenceClient;
        this.recordPublisher = recordPublisher;
        this.evaluator = evaluator;
        this.timeoutService = timeoutService;
        this.clock = clock;
    }

    public GameCreatedResponse create(String playerId, String opponentPlayerId) {
        if (playerId.equals(opponentPlayerId)) {
            throw badRequest("SAME_PLAYER", "Os jogadores da partida devem ser diferentes.");
        }
        persistenceClient.assertPlayerExists(playerId);
        persistenceClient.assertPlayerExists(opponentPlayerId);
        Instant now = clock.instant();
        String id = "game-" + UUID.randomUUID();
        Game game = new Game(id, playerId, opponentPlayerId, GameStatus.WAITING_FOR_OPPONENT,
                null, null, false, List.of(), now, now, null, null);
        Game stored = repository.mutate(id, (current, presence) -> {
            if (current != null) throw conflict("GAME_ALREADY_EXISTS", "O identificador da partida já existe.");
            return new RedisGameStateRepository.StateMutation<>(game, Map.of(), game);
        });
        publish(stored, RecordType.GAME_CREATED, now);
        return new GameCreatedResponse(id, stored.status(), playerIds(stored), stored.createdAt());
    }

    public ParticipantActionResponse join(String gameplayId, String playerId) {
        Game before = requiredGame(gameplayId);
        requireExpectedOpponent(before, playerId);
        if (before.status() == GameStatus.WAITING_FOR_OPPONENT) persistenceClient.assertPlayerExists(playerId);
        MutationResult<ParticipantActionResponse> result = repository.mutate(gameplayId, (game, presence) -> {
            requireGame(game);
            requireExpectedOpponent(game, playerId);
            if (game.status() == GameStatus.ACCEPTED && game.opponentJoined()) {
                return stateMutation(game, presence, new MutationResult<>(
                        new ParticipantActionResponse(gameplayId, game.status(), playerId, game.updatedAt()), game, null, null));
            }
            if (game.status() != GameStatus.WAITING_FOR_OPPONENT) throw conflict("INVALID_GAME_STATE", "A partida não está aguardando a entrada do oponente.");
            Instant now = clock.instant();
            Game updated = copy(game, GameStatus.ACCEPTED, game.creatorSecret(), game.opponentSecret(), true,
                    game.guesses(), now, game.activatedAt(), null);
            return stateMutation(updated, presence, new MutationResult<>(
                    new ParticipantActionResponse(gameplayId, updated.status(), playerId, now), updated, RecordType.PLAYER_JOINED, now));
        });
        publishIfChanged(result);
        return result.response();
    }

    public ParticipantActionResponse decline(String gameplayId, String playerId) {
        MutationResult<ParticipantActionResponse> result = repository.mutate(gameplayId, (game, presence) -> {
            requireGame(game);
            requireExpectedOpponent(game, playerId);
            if (game.status() != GameStatus.WAITING_FOR_OPPONENT && game.status() != GameStatus.ACCEPTED) {
                throw conflict("INVALID_GAME_STATE", "A partida não pode mais ser recusada.");
            }
            Instant now = clock.instant();
            Game updated = copy(game, GameStatus.DECLINED, game.creatorSecret(), game.opponentSecret(), game.opponentJoined(),
                    game.guesses(), now, game.activatedAt(), FinishReason.DECLINED);
            return stateMutation(updated, presence, new MutationResult<>(
                    new ParticipantActionResponse(gameplayId, updated.status(), playerId, now), updated, RecordType.GAME_DECLINED, now));
        });
        publishIfChanged(result);
        return result.response();
    }

    public SetSecretResponse setSecret(String gameplayId, String playerId, String digits) {
        try {
            evaluator.validateDigits(digits);
        } catch (IllegalArgumentException exception) {
            throw badRequest("INVALID_DIGITS", exception.getMessage());
        }
        MutationResult<SetSecretResponse> result = repository.mutate(gameplayId, (game, presence) -> {
            requireGame(game);
            requireParticipant(game, playerId);
            if (game.isOpponent(playerId) && !game.opponentJoined()) throw conflict("OPPONENT_NOT_JOINED", "O oponente ainda não entrou na partida.");
            if (game.status() != GameStatus.WAITING_FOR_OPPONENT && game.status() != GameStatus.ACCEPTED) {
                throw conflict("INVALID_GAME_STATE", "A senha não pode ser alterada neste estado da partida.");
            }
            if (game.secretFor(playerId) != null) throw conflict("SECRET_ALREADY_SET", "Este jogador já definiu uma senha.");
            String creatorSecret = game.creatorPlayerId().equals(playerId) ? digits : game.creatorSecret();
            String opponentSecret = game.opponentPlayerId().equals(playerId) ? digits : game.opponentSecret();
            boolean activates = game.opponentJoined() && creatorSecret != null && opponentSecret != null;
            Instant now = clock.instant();
            GameStatus status = activates ? GameStatus.ACTIVE : game.status();
            Game updated = copy(game, status, creatorSecret, opponentSecret, game.opponentJoined(), game.guesses(), now,
                    activates ? now : game.activatedAt(), null);
            Map<String, String> nextPresence = new HashMap<>(presence);
            if (activates) {
                nextPresence.put(updated.creatorPlayerId(), now.toString());
                nextPresence.put(updated.opponentPlayerId(), now.toString());
            }
            SetSecretResponse response = new SetSecretResponse(gameplayId, status, playerId, true, updated.bothSecretsSet());
            return new RedisGameStateRepository.StateMutation<>(updated, nextPresence,
                    new MutationResult<>(response, updated, RecordType.SECRET_SET, now));
        });
        publishIfChanged(result);
        return result.response();
    }

    public HeartbeatResponse heartbeat(String gameplayId, String playerId) {
        MutationResult<HeartbeatResponse> result = repository.mutate(gameplayId, (game, presence) -> {
            requireGame(game);
            requireParticipant(game, playerId);
            Instant now = clock.instant();
            if (game.status() == GameStatus.FINISHED || game.status() == GameStatus.DECLINED) {
                return stateMutation(game, presence, new MutationResult<>(
                        new HeartbeatResponse(gameplayId, playerId, now, game.status(), game.finishReason()), game, null, null));
            }
            if (game.status() != GameStatus.ACTIVE) throw conflict("GAME_NOT_ACTIVE", "A partida ainda não está ativa.");
            Map<String, String> nextPresence = new HashMap<>(presence);
            nextPresence.put(playerId, now.toString());
            Game updated = game;
            RecordType type = null;
            if (timeoutService.hasTimedOut(game, nextPresence, now)) {
                updated = copy(game, GameStatus.FINISHED, game.creatorSecret(), game.opponentSecret(), game.opponentJoined(),
                        game.guesses(), now, game.activatedAt(), FinishReason.TIMEOUT);
                type = RecordType.GAME_FINISHED;
            }
            HeartbeatResponse response = new HeartbeatResponse(gameplayId, playerId, now, updated.status(), updated.finishReason());
            return new RedisGameStateRepository.StateMutation<>(updated, nextPresence,
                    new MutationResult<>(response, updated, type, type == null ? null : now));
        });
        publishIfChanged(result);
        return result.response();
    }

    public GuessResponse guess(String gameplayId, String playerId, String digits) {
        try {
            evaluator.validateDigits(digits);
        } catch (IllegalArgumentException exception) {
            throw badRequest("INVALID_DIGITS", exception.getMessage());
        }
        MutationResult<GuessResponse> result = repository.mutate(gameplayId, (game, presence) -> {
            requireGame(game);
            requireParticipant(game, playerId);
            if (game.status() != GameStatus.ACTIVE) throw conflict("GAME_NOT_ACTIVE", "Só é possível enviar palpites em uma partida ativa.");
            String opponentSecret = game.secretOfOpponent(playerId);
            if (opponentSecret == null) throw conflict("OPPONENT_SECRET_MISSING", "A senha do oponente ainda não está definida.");
            Instant now = clock.instant();
            GuessResult feedback = evaluator.evaluate(digits, opponentSecret);
            Guess guess = new Guess(playerId, digits, feedback, now);
            List<Guess> guesses = new ArrayList<>(game.guesses());
            guesses.add(guess);
            boolean solved = feedback.correct() == 4;
            Game updated = copy(game, solved ? GameStatus.FINISHED : GameStatus.ACTIVE, game.creatorSecret(), game.opponentSecret(),
                    game.opponentJoined(), guesses, now, game.activatedAt(), solved ? FinishReason.SOLVED : null);
            GuessResponse response = new GuessResponse(gameplayId, playerId, digits, feedback, updated.status(), now);
            return stateMutation(updated, presence, new MutationResult<>(response, updated,
                    solved ? RecordType.GAME_FINISHED : RecordType.GUESS_RECORDED, now));
        });
        publishIfChanged(result);
        return result.response();
    }

    public GameStateResponse getState(String gameplayId) {
        Game game = requiredGame(gameplayId);
        Map<String, Boolean> secretsSet = new LinkedHashMap<>();
        secretsSet.put(game.creatorPlayerId(), game.creatorSecret() != null);
        secretsSet.put(game.opponentPlayerId(), game.opponentSecret() != null);
        return new GameStateResponse(game.gameplayId(), game.status(), playerIds(game), secretsSet, game.guesses(), game.finishReason());
    }

    private Game requiredGame(String gameplayId) {
        return repository.find(gameplayId).orElseThrow(() -> notFound("GAME_NOT_FOUND", "A partida não foi encontrada."));
    }

    private void requireGame(Game game) {
        if (game == null) throw notFound("GAME_NOT_FOUND", "A partida não foi encontrada.");
    }

    private void requireParticipant(Game game, String playerId) {
        if (!game.containsPlayer(playerId)) throw badRequest("PLAYER_NOT_IN_GAME", "O jogador não participa desta partida.");
    }

    private void requireExpectedOpponent(Game game, String playerId) {
        if (!game.isOpponent(playerId)) throw badRequest("NOT_EXPECTED_OPPONENT", "Somente o oponente convidado pode entrar ou recusar a partida.");
    }

    private Game copy(Game game, GameStatus status, String creatorSecret, String opponentSecret, boolean joined,
                      List<Guess> guesses, Instant updatedAt, Instant activatedAt, FinishReason finishReason) {
        return new Game(game.gameplayId(), game.creatorPlayerId(), game.opponentPlayerId(), status, creatorSecret,
                opponentSecret, joined, guesses, game.createdAt(), updatedAt, activatedAt, finishReason);
    }

    private <T> RedisGameStateRepository.StateMutation<MutationResult<T>> stateMutation(
            Game game, Map<String, String> presence, MutationResult<T> result) {
        return new RedisGameStateRepository.StateMutation<>(game, presence, result);
    }

    private void publishIfChanged(MutationResult<?> result) {
        if (result.recordType() != null) publish(result.game(), result.recordType(), result.timestamp());
    }

    private void publish(Game game, RecordType type, Instant timestamp) {
        recordPublisher.publish(new GameRecord(game.gameplayId(), timestamp, type, safeSnapshot(game)));
    }

    private Map<String, Object> safeSnapshot(Game game) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("status", game.status().name());
        snapshot.put("playerIds", playerIds(game));
        Map<String, Boolean> secretsSet = new LinkedHashMap<>();
        secretsSet.put(game.creatorPlayerId(), game.creatorSecret() != null);
        secretsSet.put(game.opponentPlayerId(), game.opponentSecret() != null);
        snapshot.put("secretsSet", secretsSet);
        List<Map<String, Object>> guesses = game.guesses().stream().map(guess -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("playerId", guess.playerId());
            item.put("digits", guess.digits());
            item.put("result", Map.of("correct", guess.result().correct(), "partial", guess.result().partial()));
            item.put("createdAt", guess.createdAt().toString());
            return item;
        }).toList();
        snapshot.put("guesses", guesses);
        snapshot.put("finishReason", game.finishReason() == null ? null : game.finishReason().name());
        return snapshot;
    }

    private List<String> playerIds(Game game) {
        return List.of(game.creatorPlayerId(), game.opponentPlayerId());
    }

    private GameException badRequest(String code, String message) { return new GameException(code, message, HttpStatus.BAD_REQUEST); }
    private GameException conflict(String code, String message) { return new GameException(code, message, HttpStatus.CONFLICT); }
    private GameException notFound(String code, String message) { return new GameException(code, message, HttpStatus.NOT_FOUND); }

    private record MutationResult<T>(T response, Game game, RecordType recordType, Instant timestamp) { }
}