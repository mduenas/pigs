package com.markduenas.pigstally.storage

import com.markduenas.pigstally.model.GamePreferences

expect class PreferencesStorage {
    suspend fun savePreferences(preferences: GamePreferences)
    suspend fun loadPreferences(): GamePreferences
}