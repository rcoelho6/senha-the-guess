package com.senhadeguess.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senhadeguess.mobile.data.GameOutcome
import com.senhadeguess.mobile.data.GameUiState

@Composable
fun SecretScreen(
    gameId: String,
    onBack: () -> Unit,
    onConfirm: (String) -> String?,
) {
    var digits by rememberSaveable(gameId) { mutableStateOf("") }
    var message by rememberSaveable(gameId) { mutableStateOf<String?>(null) }
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "⌫", "0", "✓")

    AppPage(title = "Sua senha", subtitle = "Só você pode ver estes dígitos", onBack = onBack) {
        ScreenHeading(
            eyebrow = "Passo 1 de 2",
            title = "Crie sua senha secreta.",
            body = "Escolha quatro algarismos diferentes. Não compartilhe esta combinação.",
            centered = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(4) { index ->
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        if (index < digits.length) "●" else "·",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            keys.chunked(3).forEach { rowKeys ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowKeys.forEach { key ->
                        OutlinedButton(
                            onClick = {
                                message = null
                                when (key) {
                                    "⌫" -> digits = digits.dropLast(1)
                                    "✓" -> Unit
                                    else -> if (digits.length < 4) {
                                        if (key in digits) message = "Cada algarismo só pode aparecer uma vez."
                                        else digits += key
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                        ) {
                            Text(key, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
        if (message != null) Text(message.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        MainAction(text = "Confirmar senha") { message = onConfirm(digits) }
        DemoNotice("A combinação fica apenas na memória desta demonstração local; não é enviada nem persistida.")
    }
}

@Composable
fun ActiveGameScreen(
    state: GameUiState,
    onBack: () -> Unit,
    onSubmitGuess: (String) -> String?,
    onSimulateOpponent: () -> String?,
    onRules: () -> Unit,
    onTimeout: () -> Unit,
) {
    var guess by rememberSaveable(state.gameId) { mutableStateOf("") }
    var message by rememberSaveable(state.gameId) { mutableStateOf<String?>(null) }

    AppPage(title = "Partida ativa", subtitle = state.gameId, onBack = onBack) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("SUA SENHA", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Text("●  ●  ●  ●", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                if (state.isMyTurn) "SUA VEZ" else "VEZ DO OPONENTE",
                color = if (state.isMyTurn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        DemoNotice("Partida demonstrativa: o estado ainda não é sincronizado com outro aparelho.")
        OutlinedTextField(
            value = guess,
            onValueChange = { value ->
                if (value.length <= 4 && value.all(Char::isDigit)) {
                    guess = value
                    message = null
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Seu palpite") },
            placeholder = { Text("Quatro algarismos distintos") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
        )
        MainAction(text = "Enviar palpite", enabled = state.isMyTurn) {
            message = onSubmitGuess(guess)
            if (message == null) guess = ""
        }
        if (message != null) Text(message.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Histórico de palpites", fontWeight = FontWeight.Bold)
            Text("${state.guesses.size} jogadas", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        if (state.guesses.isEmpty()) {
            InfoCard("Ainda sem palpites", "Seus palpites e os do oponente aparecerão aqui com as pistas de cada jogada.")
        } else {
            state.guesses.asReversed().forEach { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(if (entry.isMine) "Você" else "${entry.playerName} · oponente", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(entry.digits.chunked(1).joinToString(" "), fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 2.sp)
                        }
                        Text("${entry.correct} corretos\n${entry.misplaced} parciais", textAlign = TextAlign.End, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        SecondaryAction(
            text = if (state.isMyTurn) "Aguardando sua vez do oponente" else "Simular palpite recebido",
            enabled = !state.isMyTurn,
            onClick = { message = onSimulateOpponent() },
        )
        TextButton(onClick = onRules, modifier = Modifier.fillMaxWidth()) { Text("Ver regras") }
        TextButton(onClick = onTimeout, modifier = Modifier.fillMaxWidth()) { Text("Simular encerramento por timeout") }
    }
}

@Composable
fun ResultScreen(
    state: GameUiState,
    onHome: () -> Unit,
    onReplay: () -> Unit,
) {
    val title = when (state.outcome) {
        GameOutcome.PLAYER_WON -> "Senha descoberta!"
        GameOutcome.OPPONENT_WON -> "O oponente acertou."
        GameOutcome.TIMEOUT -> "A partida foi encerrada."
        null -> "Partida finalizada."
    }
    val kicker = when (state.outcome) {
        GameOutcome.PLAYER_WON -> "VOCÊ VENCEU"
        GameOutcome.OPPONENT_WON -> "VITÓRIA DO OPONENTE"
        GameOutcome.TIMEOUT, null -> "PARTIDA ENCERRADA"
    }
    AppPage(title = "Resultado", subtitle = state.gameId) {
        ScreenHeading(
            eyebrow = kicker,
            title = title,
            body = "${state.guesses.size} palpites registrados nesta demonstração.",
            centered = true,
        )
        InfoCard("Partida", state.gameId.ifBlank { "Sem código" })
        MainAction(text = "Voltar ao início", onClick = onHome)
        SecondaryAction(text = "Jogar novamente", onClick = onReplay)
        DemoNotice("O resultado usa somente os dados locais da demonstração.")
    }
}

@Composable
fun RulesScreen(onBack: () -> Unit) {
    AppPage(title = "Como jogar", subtitle = "Regras rápidas", onBack = onBack) {
        ScreenHeading("Objetivo", "Descubra a senha do oponente.", "Cada jogador escolhe quatro algarismos sem repetição.")
        InfoCard("Corretos", "A quantidade de dígitos certos na posição certa.")
        InfoCard("Parciais", "A quantidade de dígitos que existem na senha, mas estão em outra posição.")
        InfoCard("Exemplo", "Se a senha for 4820 e o palpite 4082, há 1 correto e 3 parciais.")
        DemoNotice("Nesta demonstração os turnos são alternados pelo app. O servidor ainda precisará confirmar e impor essa regra.")
    }
}
