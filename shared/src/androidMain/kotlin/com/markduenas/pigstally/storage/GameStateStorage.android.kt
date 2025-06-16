package com.markduenas.pigstally.storage

import android.content.Context
import com.markduenas.pigstally.model.GameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class GameStateStorage actual constructor() {
    private lateinit var context: Context
    
    constructor(context: Context) : this() {
        this.context = context
    }
    
    actual suspend fun saveGameState(gameState: GameState) {
        withContext(Dispatchers.IO) {
            try {
                val serialized = GameStateSerializer.serialize(gameState)
                val sharedPrefs = context.getSharedPreferences("pass_the_pigs", Context.MODE_PRIVATE)
                sharedPrefs.edit()
                    .putString("game_state", serialized)
                    .apply()
            } catch (e: Exception) {
                // Ignore errors for now
            }
        }
    }
    
    actual suspend fun loadGameState(): GameState? {
        return withContext(Dispatchers.IO) {
            try {
                val sharedPrefs = context.getSharedPreferences("pass_the_pigs", Context.MODE_PRIVATE)
                val serialized = sharedPrefs.getString("game_state", null)
                serialized?.let { GameStateSerializer.deserialize(it) }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    actual suspend fun clearGameState() {
        withContext(Dispatchers.IO) {
            try {
                val sharedPrefs = context.getSharedPreferences("pass_the_pigs", Context.MODE_PRIVATE)
                sharedPrefs.edit()
                    .remove("game_state")
                    .apply()
            } catch (e: Exception) {
                // Ignore errors for now
            }
        }
    }
}