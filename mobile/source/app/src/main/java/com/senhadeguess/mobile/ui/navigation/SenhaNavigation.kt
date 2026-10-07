package com.senhadeguess.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.senhadeguess.mobile.data.GameViewModel
import com.senhadeguess.mobile.ui.screens.AccessScreen
import com.senhadeguess.mobile.ui.screens.ActiveGameScreen
import com.senhadeguess.mobile.ui.screens.CreateGameScreen
import com.senhadeguess.mobile.ui.screens.HomeScreen
import com.senhadeguess.mobile.ui.screens.InviteScreen
import com.senhadeguess.mobile.ui.screens.JoinGameScreen
import com.senhadeguess.mobile.ui.screens.RegisterScreen
import com.senhadeguess.mobile.ui.screens.ResultScreen
import com.senhadeguess.mobile.ui.screens.RulesScreen
import com.senhadeguess.mobile.ui.screens.SecretScreen
import com.senhadeguess.mobile.ui.screens.WelcomeScreen

private object Route {
    const val WELCOME = "welcome"
    const val REGISTER = "register"
    const val ACCESS = "access"
    const val HOME = "home"
    const val CREATE = "create"
    const val INVITE = "invite"
    const val JOIN = "join"
    const val SECRET = "secret"
    const val GAME = "game"
    const val RESULT = "result"
    const val RULES = "rules"
}

@Composable
fun SenhaNavigation(
    navController: NavHostController = rememberNavController(),
    gameViewModel: GameViewModel = viewModel(),
) {
    val state = gameViewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(state.outcome) {
        if (state.outcome != null && navController.currentDestination?.route == Route.GAME) {
            navController.navigate(Route.RESULT) {
                popUpTo(Route.GAME) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = Route.WELCOME) {
        composable(Route.WELCOME) {
            WelcomeScreen(
                onRegister = { navController.navigate(Route.REGISTER) },
                onAccess = { navController.navigate(Route.ACCESS) },
            )
        }
        composable(Route.REGISTER) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onRegister = { name ->
                    val error = gameViewModel.register(name)
                    if (error == null) navController.openHomeFromWelcome()
                    error
                },
            )
        }
        composable(Route.ACCESS) {
            AccessScreen(
                onBack = { navController.popBackStack() },
                onAccess = { playerId ->
                    val error = gameViewModel.signIn(playerId)
                    if (error == null) navController.openHomeFromWelcome()
                    error
                },
            )
        }
        composable(Route.HOME) {
            HomeScreen(
                state = state,
                onCreate = { navController.navigate(Route.CREATE) },
                onJoin = { navController.navigate(Route.JOIN) },
                onRules = { navController.navigate(Route.RULES) },
            )
        }
        composable(Route.CREATE) {
            CreateGameScreen(
                onBack = { navController.popBackStack() },
                onCreate = { opponentId ->
                    val error = gameViewModel.createGame(opponentId)
                    if (error == null) navController.navigate(Route.INVITE)
                    error
                },
            )
        }
        composable(Route.INVITE) {
            InviteScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onSetSecret = { navController.navigate(Route.SECRET) },
                onOpponentJoined = {
                    gameViewModel.markOpponentJoined()
                    if (state.mySecret.isBlank()) navController.navigate(Route.SECRET)
                    else navController.navigate(Route.GAME)
                },
                onHome = { navController.openHome() },
            )
        }
        composable(Route.JOIN) {
            JoinGameScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onJoin = { id ->
                    val error = gameViewModel.joinGame(id)
                    if (error == null) navController.navigate(Route.SECRET)
                    error
                },
                onDecline = { navController.openHome() },
            )
        }
        composable(Route.SECRET) {
            SecretScreen(
                gameId = state.gameId,
                onBack = { navController.popBackStack() },
                onConfirm = { secret ->
                    val error = gameViewModel.setSecret(secret)
                    if (error == null) {
                        if (state.opponentJoined) navController.navigate(Route.GAME)
                        else navController.openInvite()
                    }
                    error
                },
            )
        }
        composable(Route.GAME) {
            ActiveGameScreen(
                state = state,
                onBack = { navController.openHome() },
                onSubmitGuess = gameViewModel::submitGuess,
                onSimulateOpponent = gameViewModel::simulateOpponentGuess,
                onRules = { navController.navigate(Route.RULES) },
                onTimeout = gameViewModel::finishByTimeout,
            )
        }
        composable(Route.RESULT) {
            ResultScreen(
                state = state,
                onHome = {
                    gameViewModel.startNewGame()
                    navController.openHome()
                },
                onReplay = {
                    gameViewModel.replayGame()
                    navController.navigate(Route.GAME) { launchSingleTop = true }
                },
            )
        }
        composable(Route.RULES) {
            RulesScreen(onBack = { navController.popBackStack() })
        }
    }
}

private fun NavHostController.openHome() {
    navigate(Route.HOME) {
        popUpTo(Route.HOME) { inclusive = false }
        launchSingleTop = true
    }
}

private fun NavHostController.openHomeFromWelcome() {
    navigate(Route.HOME) {
        popUpTo(Route.WELCOME) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.openInvite() {
    navigate(Route.INVITE) {
        popUpTo(Route.INVITE) { inclusive = false }
        launchSingleTop = true
    }
}
