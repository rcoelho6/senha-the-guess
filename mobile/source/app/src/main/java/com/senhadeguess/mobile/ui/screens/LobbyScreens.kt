package com.senhadeguess.mobile.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senhadeguess.mobile.data.GameUiState

@Composable
fun HomeScreen(
    state: GameUiState,
    onCreate: () -> Unit,
    onJoin: () -> Unit,
    onRules: () -> Unit,
) {
    AppPage(title = "Início", subtitle = "Olá, ${state.displayName}") {
        ScreenHeading(
            eyebrow = "Seu espaço de jogo",
            title = "Pronta para um desafio?",
            body = "Crie uma partida ou entre com um código que recebeu.",
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(18.dp),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(state.displayName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(state.playerId, fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        MainAction(text = "＋  Criar partida", onClick = onCreate)
        SecondaryAction(text = "Entrar em partida", onClick = onJoin)
        InfoCard("Como funciona", "Cada pessoa escolhe uma senha de quatro algarismos diferentes. Faça palpites e use as pistas para descobrir o código adversário.")
        TextButton(onClick = onRules, modifier = Modifier.fillMaxWidth()) { Text("Ver regras do jogo") }
    }
}

@Composable
fun CreateGameScreen(
    onBack: () -> Unit,
    onCreate: (String) -> String?,
) {
    var opponentId by rememberSaveable { mutableStateOf("player-456") }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    AppPage(title = "Nova partida", subtitle = "Escolha quem desafiar", onBack = onBack) {
        ScreenHeading(
            eyebrow = "Desafiar alguém",
            title = "Quem será seu oponente?",
            body = "Informe o ID de jogador da pessoa que vai receber o convite.",
        )
        OutlinedTextField(
            value = opponentId,
            onValueChange = { opponentId = it; message = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("ID do oponente") },
            singleLine = true,
        )
        InfoCard("Regras da partida", "Duelo individual. Cada jogador escolhe uma senha de quatro dígitos distintos e tenta descobrir a senha do adversário.")
        if (message != null) Text(message.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        MainAction(text = "Criar e gerar convite") { message = onCreate(opponentId) }
        DemoNotice("A criação é simulada neste app. Ainda não há comunicação com o servidor.")
    }
}

@Composable
fun InviteScreen(
    state: GameUiState,
    onBack: () -> Unit,
    onSetSecret: () -> Unit,
    onOpponentJoined: () -> Unit,
    onHome: () -> Unit,
) {
    val context = LocalContext.current
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    AppPage(title = "Convite enviado", subtitle = "Aguardando o oponente", onBack = onBack) {
        ScreenHeading(
            eyebrow = "Sua partida está quase pronta",
            title = if (state.mySecret.isBlank()) "Compartilhe o código." else "Sua senha está definida.",
            body = "Envie o código ao oponente. Você pode preparar sua senha enquanto aguarda.",
            centered = true,
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
            shape = RoundedCornerShape(18.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("CÓDIGO DA PARTIDA", color = MaterialTheme.colorScheme.primaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Text(state.gameId, color = MaterialTheme.colorScheme.surface, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Código da partida", state.gameId))
                        message = "Código copiado."
                    }) { Text("Copiar") }
                    TextButton(onClick = { shareInvite(context, state.gameId) }) { Text("Compartilhar") }
                }
            }
        }
        if (state.mySecret.isBlank()) {
            MainAction(text = "Definir minha senha", onClick = onSetSecret)
        } else {
            InfoCard("Senha definida", "Sua combinação está guardada apenas no estado local desta demonstração.")
        }
        if (!state.opponentJoined) {
            SecondaryAction(text = "Simular entrada do oponente", onClick = onOpponentJoined)
        } else {
            MainAction(text = "Ir para a partida", onClick = onOpponentJoined)
        }
        if (message != null) Text(message.orEmpty(), color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        TextButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) { Text("Voltar ao início") }
        DemoNotice("A entrada do oponente é simulada. O código não é validado pelo backend nesta versão.")
    }
}

@Composable
fun JoinGameScreen(
    state: GameUiState,
    onBack: () -> Unit,
    onJoin: (String) -> String?,
    onDecline: () -> Unit,
) {
    var gameId by rememberSaveable { mutableStateOf(state.gameId) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    AppPage(title = "Entrar em partida", subtitle = "Use o convite recebido", onBack = onBack) {
        ScreenHeading(
            eyebrow = "Código do convite",
            title = "Vamos jogar.",
            body = "Cole o código que o criador compartilhou com você.",
        )
        OutlinedTextField(
            value = gameId,
            onValueChange = { gameId = it; message = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Código da partida") },
            singleLine = true,
        )
        InfoCard("Você entrará como", "${state.displayName} · ${state.playerId}")
        if (message != null) Text(message.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        MainAction(text = "Entrar na partida") { message = onJoin(gameId) }
        SecondaryAction(text = "Recusar este convite", onClick = onDecline)
        DemoNotice("Entrada e recusa são apenas navegação local. Códigos reais ainda não são consultados.")
    }
}

private fun shareInvite(context: Context, gameId: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Vamos jogar Senha! Código da partida: $gameId")
    }
    context.startActivity(Intent.createChooser(sendIntent, "Compartilhar convite"))
}
