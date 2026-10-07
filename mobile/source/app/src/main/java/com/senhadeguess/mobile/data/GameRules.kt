package com.senhadeguess.mobile.data

data class GuessFeedback(
    val correct: Int,
    val misplaced: Int,
)

fun isValidCode(value: String): Boolean =
    value.length == 4 && value.all(Char::isDigit) && value.toSet().size == 4

/** Calcula quantos dígitos estão na posição certa e quantos estão em outra posição. */
fun evaluateGuess(guess: String, secret: String): GuessFeedback {
    require(isValidCode(guess)) { "O palpite deve ter quatro algarismos distintos." }
    require(isValidCode(secret)) { "A senha deve ter quatro algarismos distintos." }

    val correct = guess.indices.count { guess[it] == secret[it] }
    val commonDigits = guess.count { it in secret }
    return GuessFeedback(correct = correct, misplaced = commonDigits - correct)
}
