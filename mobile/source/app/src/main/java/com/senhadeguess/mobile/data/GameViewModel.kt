package com.senhadeguess.mobile.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Estado local de demonstração. As chamadas a REST, autenticação e presença serão
 * conectadas quando o contrato do backend e as regras de turnos estiverem definidos.
 */
class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val demoOpponentSecret = "4820"

    fun register(displayName: String): String? {
        val name = displayName.trim()
        if (name.isBlank()) return "Informe um nome para continuar."
        _uiState.value = _uiState.value.copy(
            displayName = name,
            playerId = "player-demo",
        )
        return null
    }

    fun signIn(playerId: String): String? {
        val id = playerId.trim()
        if (id.isBlank()) return "Informe um ID de jogador."
        _uiState.value = _uiState.value.copy(
            playerId = id,
            displayName = when (id) {
                "player-123" -> "Nina"
                "player-456" -> "Alex"
                else -> "Jogador"
            },
        )
        return null
    }

    fun createGame(opponentId: String): String? {
        val opponent = opponentId.trim()
        if (opponent.isBlank()) return "Informe o ID do oponente."
        if (opponent == _uiState.value.playerId) return "Escolha um ID diferente do seu."
        _uiState.value = _uiState.value.copy(
            opponentId = opponent,
            gameId = "game-${UUID.randomUUID().toString().take(6)}",
            mySecret = "",
            opponentJoined = false,
            isMyTurn = true,
            guesses = emptyList(),
            outcome = null,
        )
        return null
    }

    fun joinGame(gameId: String): String? {
        val id = gameId.trim()
        if (id.isBlank()) return "Informe o código da partida."
        _uiState.value = _uiState.value.copy(
            gameId = id,
            opponentJoined = true,
            mySecret = "",
            isMyTurn = true,
            guesses = emptyList(),
            outcome = null,
        )
        return null
    }

    fun markOpponentJoined() {
        _uiState.value = _uiState.value.copy(opponentJoined = true)
    }

    fun setSecret(secret: String): String? {
        if (!isValidCode(secret)) return "Use quatro algarismos diferentes."
        _uiState.value = _uiState.value.copy(
            mySecret = secret,
            isMyTurn = true,
            guesses = emptyList(),
            outcome = null,
        )
        return null
    }

    fun submitGuess(value: String): String? {
        val state = _uiState.value
        if (!state.opponentJoined || state.mySecret.isBlank()) {
            return "A partida ainda está aguardando os dois jogadores."
        }
        if (state.outcome != null) return "Esta partida já foi encerrada."
        if (!state.isMyTurn) return "Aguarde o palpite do oponente."
        if (!isValidCode(value)) return "Use quatro algarismos diferentes."

        val feedback = evaluateGuess(value, demoOpponentSecret)
        val entry = GuessEntry(
            playerName = state.displayName,
            digits = value,
            correct = feedback.correct,
            misplaced = feedback.misplaced,
            isMine = true,
        )
        _uiState.value = state.copy(
            guesses = state.guesses + entry,
            isMyTurn = feedback.correct != 4,
            outcome = if (feedback.correct == 4) GameOutcome.PLAYER_WON else null,
        )
        return null
    }

    fun simulateOpponentGuess(): String? {
        val state = _uiState.value
        if (state.isMyTurn) return "Envie seu palpite antes de simular a vez do oponente."
        if (state.outcome != null) return "Esta partida já foi encerrada."
        if (!isValidCode(state.mySecret)) return "Defina sua senha antes de iniciar."

        val simulatedGuess = "7139"
        val feedback = evaluateGuess(simulatedGuess, state.mySecret)
        val entry = GuessEntry(
            playerName = state.opponentId,
            digits = simulatedGuess,
            correct = feedback.correct,
            misplaced = feedback.misplaced,
            isMine = false,
        )
        _uiState.value = state.copy(
            guesses = state.guesses + entry,
            isMyTurn = feedback.correct != 4,
            outcome = if (feedback.correct == 4) GameOutcome.OPPONENT_WON else null,
        )
        return null
    }

    fun finishByTimeout() {
        _uiState.value = _uiState.value.copy(outcome = GameOutcome.TIMEOUT)
    }

    fun replayGame() {
        _uiState.value = _uiState.value.copy(
            guesses = emptyList(),
            isMyTurn = true,
            outcome = null,
        )
    }

    fun startNewGame() {
        _uiState.value = _uiState.value.copy(
            opponentId = "",
            gameId = "",
            mySecret = "",
            opponentJoined = false,
            isMyTurn = true,
            guesses = emptyList(),
            outcome = null,
        )
    }
}
