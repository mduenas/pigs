package com.markduenas.pigstally.storage

import com.markduenas.pigstally.model.GamePreferences
import platform.Foundation.NSUserDefaults

actual class PreferencesStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    actual suspend fun savePreferences(preferences: GamePreferences) {
        userDefaults.setInteger(preferences.defaultPlayerCount.toLong(), "default_player_count")
        userDefaults.setInteger(preferences.winningScore.toLong(), "winning_score")
        userDefaults.synchronize()
    }
    
    actual suspend fun loadPreferences(): GamePreferences {
        val defaultPlayerCount = userDefaults.integerForKey("default_player_count").let { 
            if (it == 0L) 2 else it.toInt() 
        }
        val winningScore = userDefaults.integerForKey("winning_score").let { 
            if (it == 0L) 100 else it.toInt() 
        }
        
        return GamePreferences(
            defaultPlayerCount = defaultPlayerCount,
            winningScore = winningScore
        )
    }
}