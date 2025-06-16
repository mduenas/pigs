package com.markduenas.pigstally.model

data class GameState(
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val currentTurnScore: Int = 0,
    val currentTurnState: TurnState = TurnState(),
    val gameStarted: Boolean = false,
    val gameEnded: Boolean = false,
    val winner: Player? = null,
    val roundNumber: Int = 1,
    val winningScore: Int = 100
) {
    val currentPlayer: Player?
        get() = players.getOrNull(currentPlayerIndex)
    
    val nextPlayer: Player?
        get() {
            val nextIndex = getNextPlayerIndex()
            return if (nextIndex != -1) players.getOrNull(nextIndex) else null
        }
    
    val activePlayers: List<Player>
        get() = players.filter { !it.isEliminated }
    
    private fun getNextPlayerIndex(): Int {
        val activePlayers = players.filter { !it.isEliminated }
        if (activePlayers.size <= 1) return -1
        
        var nextIndex = (currentPlayerIndex + 1) % players.size
        while (nextIndex != currentPlayerIndex && players[nextIndex].isEliminated) {
            nextIndex = (nextIndex + 1) % players.size
        }
        
        return if (players[nextIndex].isEliminated) -1 else nextIndex
    }
    
    fun canContinueGame(): Boolean {
        return activePlayers.size > 1 && !gameEnded && winner == null
    }
}