package com.senhadeguess.mobile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.senhadeguess.mobile.ui.screens.WelcomeScreen
import com.senhadeguess.mobile.ui.theme.SenhaAppTheme
import org.junit.Rule
import org.junit.Test

class WelcomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun welcomeShowsPrimaryActions() {
        composeRule.setContent {
            SenhaAppTheme {
                WelcomeScreen(onRegister = {}, onAccess = {})
            }
        }
        composeRule.onNodeWithText("Criar meu perfil").assertIsDisplayed()
        composeRule.onNodeWithText("Já tenho um ID de jogador").assertIsDisplayed()
    }
}
