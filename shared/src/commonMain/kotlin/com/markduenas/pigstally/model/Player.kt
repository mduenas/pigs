package com.markduenas.pigstally.model

data class Player(
    val id: String,
    val name: String,
    val totalScore: Int = 0,
    val isEliminated: Boolean = false
) {
    val isWinner: Boolean
        get() = totalScore >= 100 && !isEliminated
    
    val progressPercentage: Float
        get() = if (totalScore >= 100) 1f else totalScore / 100f
    
    companion object {
        fun createDefault(playerId: Int): Player {
            return Player(
                id = "player_$playerId",
                name = "Player $playerId"
            )
        }
    }
}