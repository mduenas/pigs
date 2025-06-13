package com.passthepigs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.passthepigs.game.GameViewModel
import com.passthepigs.ui.screens.GameScreen
import com.passthepigs.ui.screens.GameSetupScreen
import com.passthepigs.ui.theme.PassThePigsColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val viewModel = remember { GameViewModel() }
    val gameState by viewModel.gameState.collectAsState()
    
    var currentScreen by remember { 
        mutableStateOf(if (gameState.gameStarted) "game" else "setup") 
    }
    
    // Update screen based on game state
    LaunchedEffect(gameState.gameStarted) {
        if (!gameState.gameStarted) {
            currentScreen = "setup"
        }
    }
    
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = PassThePigsColors.Primary,
            primaryContainer = PassThePigsColors.Primary.copy(alpha = 0.1f),
            secondary = PassThePigsColors.Secondary,
            secondaryContainer = PassThePigsColors.Secondary.copy(alpha = 0.1f),
            background = PassThePigsColors.Background,
            surface = PassThePigsColors.Surface,
            onPrimary = PassThePigsColors.OnPrimary,
            onSecondary = PassThePigsColors.OnSecondary,
            onBackground = PassThePigsColors.OnBackground,
            onSurface = PassThePigsColors.OnSurface
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = PassThePigsColors.Background
        ) { paddingValues ->
            when (currentScreen) {
                "setup" -> {
                    GameSetupScreen(
                        gameState = gameState,
                        viewModel = viewModel,
                        onStartGame = { currentScreen = "game" },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                "game" -> {
                    GameScreen(
                        gameState = gameState,
                        viewModel = viewModel,
                        onNavigateToSetup = { currentScreen = "setup" },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}