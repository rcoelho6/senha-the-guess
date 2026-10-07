package com.senhadeguess.mobile.data

data class GuessEntry(
    val playerName: String,
    val digits: String,
    val correct: Int,
    val misplaced: Int,
    val isMine: Boolean,
)

enum class GameOutcome {
    PLAYER_WON,
    OPPONENT_WON,
    TIMEOUT,
}

data class GameUiState(
    val displayName: String = "Nina",
    val playerId: String = "player-123",
    val opponentId: String = "player-456",
    val gameId: String = "game-8b72c1",
    val mySecret: String = "",
    val opponentJoined: Boolean = false,
    val isMyTurn: Boolean = true,
    val guesses: List<GuessEntry> = emptyList(),
    val outcome: GameOutcome? = null,
)
