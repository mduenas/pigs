package com.passthepigs.storage

import com.passthepigs.model.GamePreferences

expect class PreferencesStorage {
    suspend fun savePreferences(preferences: GamePreferences)
    suspend fun loadPreferences(): GamePreferences
}