package com.markduenas.pigstally.storage

import com.markduenas.pigstally.model.GameState
import com.markduenas.pigstally.model.Player

expect class GameStateStorage() {
    suspend fun saveGameState(gameState: GameState)
    suspend fun loadGameState(): GameState?
    suspend fun clearGameState()
}

object GameStateSerializer {
    fun serialize(gameState: GameState): String {
        val playersJson = gameState.players.joinToString(";") { player ->
            "${player.id},${player.name},${player.totalScore},${player.isEliminated}"
        }
        
        return listOf(
            playersJson,
            gameState.currentPlayerIndex.toString(),
            gameState.currentTurnScore.toString(),
            gameState.gameStarted.toString(),
            gameState.gameEnded.toString(),
            gameState.winner?.id ?: "",
            gameState.roundNumber.toString()
        ).joinToString("|")
    }
    
    fun deserialize(data: String): GameState? {
        return try {
            val parts = data.split("|")
            if (parts.size < 7) return null
            
            val players = if (parts[0].isNotEmpty()) {
                parts[0].split(";").map { playerData ->
                    val playerParts = playerData.split(",")
                    if (playerParts.size < 4) return null
                    
                    Player(
                        id = playerParts[0],
                        name = playerParts[1],
                        totalScore = playerParts[2].toIntOrNull() ?: 0,
                        isEliminated = playerParts[3].toBooleanStrictOrNull() ?: false
                    )
                }
            } else {
                emptyList()
            }
            
            val winnerId = parts[5].takeIf { it.isNotEmpty() }
            val winner = winnerId?.let { id -> players.find { it.id == id } }
            
            GameState(
                players = players,
                currentPlayerIndex = parts[1].toIntOrNull() ?: 0,
                currentTurnScore = parts[2].toIntOrNull() ?: 0,
                gameStarted = parts[3].toBooleanStrictOrNull() ?: false,
                gameEnded = parts[4].toBooleanStrictOrNull() ?: false,
                winner = winner,
                roundNumber = parts[6].toIntOrNull() ?: 1
            )
        } catch (e: Exception) {
            null
        }
    }
}