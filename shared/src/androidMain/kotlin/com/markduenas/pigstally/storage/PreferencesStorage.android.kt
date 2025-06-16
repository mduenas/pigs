package com.markduenas.pigstally.storage

import android.content.Context
import android.content.SharedPreferences
import com.markduenas.pigstally.model.GamePreferences

actual class PreferencesStorage(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
    
    actual suspend fun savePreferences(preferences: GamePreferences) {
        prefs.edit()
            .putInt("default_player_count", preferences.defaultPlayerCount)
            .putInt("winning_score", preferences.winningScore)
            .apply()
    }
    
    actual suspend fun loadPreferences(): GamePreferences {
        return GamePreferences(
            defaultPlayerCount = prefs.getInt("default_player_count", 2),
            winningScore = prefs.getInt("winning_score", 100)
        )
    }
}