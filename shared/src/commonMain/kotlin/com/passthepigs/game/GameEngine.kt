package com.passthepigs.game

import com.passthepigs.model.*

class GameEngine {
    
    fun processAction(currentState: GameState, action: GameAction): GameState {
        return when (action) {
            is GameAction.AddPlayer -> addPlayer(currentState, action.name)
            is GameAction.RemovePlayer -> removePlayer(currentState, action.playerId)
            is GameAction.UpdatePlayerName -> updatePlayerName(currentState, action.playerId, action.newName)
            is GameAction.ReorderPlayers -> currentState.copy(players = action.newOrder)
            
            GameAction.StartGame -> startGame(currentState)
            GameAction.NewGame -> newGame()
            
            is GameAction.Score -> processScore(currentState, action.position, action.customPoints)
            GameAction.BankPoints -> bankPoints(currentState)
            GameAction.UndoLastRoll -> undoLastRoll(currentState)
        }
    }
    
    private fun addPlayer(state: GameState, name: String): GameState {
        val playerId = "player_${kotlin.random.Random.nextInt(10000)}"
        val newPlayer = Player(
            id = playerId,
            name = name.ifBlank { "Player ${state.players.size + 1}" }
        )
        return state.copy(players = state.players + newPlayer)
    }
    
    private fun removePlayer(state: GameState, playerId: String): GameState {
        val newPlayers = state.players.filter { it.id != playerId }
        val newCurrentIndex = if (state.currentPlayerIndex >= newPlayers.size) {
            0
        } else {
            state.currentPlayerIndex
        }
        
        return state.copy(
            players = newPlayers,
            currentPlayerIndex = newCurrentIndex
        )
    }
    
    private fun updatePlayerName(state: GameState, playerId: String, newName: String): GameState {
        val updatedPlayers = state.players.map { player ->
            if (player.id == playerId) {
                player.copy(name = newName.ifBlank { player.name })
            } else {
                player
            }
        }
        return state.copy(players = updatedPlayers)
    }
    
    private fun startGame(state: GameState): GameState {
        return if (state.players.size >= 2) {
            state.copy(gameStarted = true)
        } else {
            state
        }
    }
    
    private fun newGame(): GameState {
        return GameState(
            players = listOf(
                Player.createDefault(1),
                Player.createDefault(2)
            )
        )
    }
    
    private fun processScore(state: GameState, position: ScoringPosition, customPoints: Int): GameState {
        if (!state.gameStarted || state.gameEnded) return state
        
        val currentPlayer = state.currentPlayer ?: return state
        
        return when {
            position.isPenalty -> processPenalty(state, position)
            position == ScoringPosition.MIXED -> processMixedScore(state, customPoints)
            else -> processNormalScore(state, position.points)
        }
    }
    
    private fun processPenalty(state: GameState, position: ScoringPosition): GameState {
        val currentPlayer = state.currentPlayer ?: return state
        
        return when (position) {
            ScoringPosition.PIG_OUT -> {
                // Lose turn score, advance to next player
                advanceToNextPlayer(state.copy(currentTurnScore = 0))
            }
            ScoringPosition.OINKER -> {
                // Reset total score to 0, advance to next player
                val updatedPlayers = state.players.map { player ->
                    if (player.id == currentPlayer.id) {
                        player.copy(totalScore = 0)
                    } else {
                        player
                    }
                }
                advanceToNextPlayer(
                    state.copy(
                        players = updatedPlayers,
                        currentTurnScore = 0
                    )
                )
            }
            ScoringPosition.PIGGYBACK -> {
                // Eliminate player, advance to next player
                val updatedPlayers = state.players.map { player ->
                    if (player.id == currentPlayer.id) {
                        player.copy(isEliminated = true)
                    } else {
                        player
                    }
                }
                val newState = state.copy(players = updatedPlayers, currentTurnScore = 0)
                advanceToNextPlayer(newState)
            }
            else -> state
        }
    }
    
    private fun processMixedScore(state: GameState, points: Int): GameState {
        return state.copy(currentTurnScore = state.currentTurnScore + points)
    }
    
    private fun processNormalScore(state: GameState, points: Int): GameState {
        return state.copy(currentTurnScore = state.currentTurnScore + points)
    }
    
    private fun bankPoints(state: GameState): GameState {
        if (!state.gameStarted || state.gameEnded || state.currentTurnScore == 0) return state
        
        val currentPlayer = state.currentPlayer ?: return state
        val newTotalScore = currentPlayer.totalScore + state.currentTurnScore
        
        val updatedPlayers = state.players.map { player ->
            if (player.id == currentPlayer.id) {
                player.copy(totalScore = newTotalScore)
            } else {
                player
            }
        }
        
        val newState = state.copy(
            players = updatedPlayers,
            currentTurnScore = 0
        )
        
        // Check for winner
        val winner = updatedPlayers.find { it.totalScore >= 100 && !it.isEliminated }
        if (winner != null) {
            return newState.copy(
                gameEnded = true,
                winner = winner
            )
        }
        
        return advanceToNextPlayer(newState)
    }
    
    private fun advanceToNextPlayer(state: GameState): GameState {
        val activePlayers = state.players.filter { !it.isEliminated }
        
        if (activePlayers.size <= 1) {
            val winner = activePlayers.firstOrNull()
            return state.copy(
                gameEnded = true,
                winner = winner
            )
        }
        
        var nextIndex = (state.currentPlayerIndex + 1) % state.players.size
        while (nextIndex != state.currentPlayerIndex && state.players[nextIndex].isEliminated) {
            nextIndex = (nextIndex + 1) % state.players.size
        }
        
        val newRoundNumber = if (nextIndex <= state.currentPlayerIndex) {
            state.roundNumber + 1
        } else {
            state.roundNumber
        }
        
        return state.copy(
            currentPlayerIndex = nextIndex,
            roundNumber = newRoundNumber
        )
    }
    
    private fun undoLastRoll(state: GameState): GameState {
        // Simple implementation: just reset current turn score
        return state.copy(currentTurnScore = 0)
    }
}