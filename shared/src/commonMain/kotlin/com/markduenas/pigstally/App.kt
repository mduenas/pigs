package com.markduenas.pigstally

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.markduenas.pigstally.game.GameViewModel
import com.markduenas.pigstally.ui.screens.GameScreen
import com.markduenas.pigstally.ui.screens.GameSetupScreen
import com.markduenas.pigstally.ui.screens.SettingsScreen
import com.markduenas.pigstally.ui.theme.PassThePigsColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val viewModel = remember { GameViewModel() }
    val gameState by viewModel.gameState.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    
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
            containerColor = PassThePigsColors.Background,
            topBar = {
                if (currentScreen == "setup") {
                    TopAppBar(
                        title = { 
                            Text(
                                text = "Pigs Tally",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PassThePigsColors.Primary
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = { currentScreen = "settings" }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = PassThePigsColors.Primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = PassThePigsColors.Background
                        )
                    )
                }
            }
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
                        onNavigateToSettings = { currentScreen = "settings" },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                "settings" -> {
                    SettingsScreen(
                        preferences = preferences,
                        onPreferencesChange = { newPreferences ->
                            viewModel.updatePreferences(newPreferences)
                        },
                        onNavigateBack = { 
                            currentScreen = if (gameState.gameStarted) "game" else "setup"
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}