package com.markduenas.pigstally.model

data class GamePreferences(
    val defaultPlayerCount: Int = 2,
    val winningScore: Int = 100
) {
    companion object {
        val DEFAULT = GamePreferences()
    }
    
    fun isValid(): Boolean {
        return defaultPlayerCount in 2..6 && winningScore in 50..500
    }
}