package com.senhadeguess.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onRegister: () -> Unit,
    onAccess: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Spacer(Modifier.height(18.dp))
        BrandDigits()
        Text("DESAFIE A LÓGICA", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Text("Descubra a senha secreta.", fontSize = 29.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
        Text("Escolha quatro dígitos, leia as pistas e descubra o código do outro jogador.", fontSize = 15.sp, lineHeight = 22.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(10.dp))
        MainAction(text = "Criar meu perfil", onClick = onRegister)
        SecondaryAction(text = "Já tenho um ID de jogador", onClick = onAccess)
        DemoNotice("Esta primeira versão funciona localmente. Cadastro e partidas ainda não são enviados a um servidor.")
    }
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegister: (String) -> String?,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmation by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    AppPage(title = "Criar perfil", subtitle = "Uma identidade para jogar", onBack = onBack) {
        ScreenHeading(
            eyebrow = "Cadastro demonstrativo",
            title = "Bem-vinda ao jogo.",
            body = "Preencha seus dados para experimentar o fluxo no aparelho.",
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; message = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nome de exibição") },
            singleLine = true,
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; message = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("E-mail") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; message = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Criar senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
        )
        OutlinedTextField(
            value = confirmation,
            onValueChange = { confirmation = it; message = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Confirmar senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
        )
        if (message != null) {
            Text(message.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }
        MainAction(text = "Criar perfil (demo)") {
            message = when {
                name.isBlank() -> "Informe seu nome."
                !email.contains('@') -> "Informe um e-mail válido."
                password.length < 8 -> "Use pelo menos 8 caracteres na senha."
                password != confirmation -> "As senhas não coincidem."
                else -> onRegister(name)
            }
        }
        DemoNotice("O perfil é temporário e fica somente na memória do app. Este formulário ainda não cria uma conta real.")
    }
}

@Composable
fun AccessScreen(
    onBack: () -> Unit,
    onAccess: (String) -> String?,
) {
    var playerId by rememberSaveable { mutableStateOf("player-123") }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    AppPage(title = "Acessar com ID", subtitle = "Perfil de jogador existente", onBack = onBack) {
        ScreenHeading(
            eyebrow = "Acesso de demonstração",
            title = "Que bom ter você de volta.",
            body = "Use um ID local para experimentar a navegação do app.",
        )
        OutlinedTextField(
            value = playerId,
            onValueChange = { playerId = it; message = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("ID do jogador") },
            singleLine = true,
        )
        Text("IDs de exemplo: player-123, player-456 ou qualquer texto.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        if (message != null) Text(message.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        MainAction(text = "Continuar") { message = onAccess(playerId) }
        DemoNotice("O backend atual ainda não autentica o jogador. O acesso nesta tela é apenas local.")
    }
}
