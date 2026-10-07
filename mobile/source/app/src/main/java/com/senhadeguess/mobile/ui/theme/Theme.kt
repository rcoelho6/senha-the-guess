package com.senhadeguess.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SenhaColors = lightColorScheme(
    primary = Color(0xFF0E7658),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F4E6),
    onPrimaryContainer = Color(0xFF073E2C),
    secondary = Color(0xFF52647B),
    onSecondary = Color.White,
    background = Color(0xFFF4F7FB),
    onBackground = Color(0xFF142039),
    surface = Color.White,
    onSurface = Color(0xFF142039),
    surfaceVariant = Color(0xFFE8EDF3),
    onSurfaceVariant = Color(0xFF526079),
    error = Color(0xFFB5473B),
    onError = Color.White,
)

@Composable
fun SenhaAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SenhaColors,
        content = content,
    )
}
