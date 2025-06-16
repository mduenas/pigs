package com.markduenas.pigstally.storage

import com.markduenas.pigstally.model.GameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults

actual class GameStateStorage actual constructor() {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val gameStateKey = "pass_the_pigs_game_state"
    
    actual suspend fun saveGameState(gameState: GameState) {
        withContext(Dispatchers.Main) {
            try {
                val serialized = GameStateSerializer.serialize(gameState)
                userDefaults.setObject(serialized, gameStateKey)
                userDefaults.synchronize()
            } catch (e: Exception) {
                // Ignore errors for now
            }
        }
    }
    
    actual suspend fun loadGameState(): GameState? {
        return withContext(Dispatchers.Main) {
            try {
                val serialized = userDefaults.stringForKey(gameStateKey)
                serialized?.let { GameStateSerializer.deserialize(it) }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    actual suspend fun clearGameState() {
        withContext(Dispatchers.Main) {
            try {
                userDefaults.removeObjectForKey(gameStateKey)
                userDefaults.synchronize()
            } catch (e: Exception) {
                // Ignore errors for now
            }
        }
    }
}