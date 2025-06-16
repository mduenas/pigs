package com.passthepigs

import androidx.compose.ui.window.ComposeUIViewController
import com.passthepigs.game.GameViewModel
import com.passthepigs.storage.GameStateStorage
import com.passthepigs.storage.PreferencesStorage

fun MainViewController() = ComposeUIViewController { 
    App() 
}