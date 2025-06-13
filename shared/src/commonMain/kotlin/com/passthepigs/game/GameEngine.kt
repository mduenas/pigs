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
            
            is GameAction.ScorePig -> processScorePig(currentState, action.position)
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
    
    private fun processScorePig(state: GameState, position: ScoringPosition): GameState {
        if (!state.gameStarted || state.gameEnded) return state
        if (state.currentTurnState.isComplete) return state
        
        val currentTurnState = state.currentTurnState
        val pigRoll = PigRoll(position)
        
        val newTurnState = when (currentTurnState.currentPigNumber) {
            1 -> currentTurnState.copy(pig1 = pigRoll)
            2 -> currentTurnState.copy(pig2 = pigRoll, isComplete = true)
            else -> currentTurnState // Should not happen
        }
        
        val newState = state.copy(currentTurnState = newTurnState)
        
        // If turn is complete, process the results
        return if (newTurnState.isComplete) {
            processTurnComplete(newState)
        } else {
            newState
        }
    }
    
    private fun processTurnComplete(state: GameState): GameState {
        val turnState = state.currentTurnState
        val currentPlayer = state.currentPlayer ?: return state
        
        return when {
            turnState.hasPenalty -> processPenaltyTurn(state, turnState)
            else -> {
                // Add points to current turn score
                val newTurnScore = state.currentTurnScore + turnState.finalPoints
                state.copy(currentTurnScore = newTurnScore)
            }
        }
    }
    
    private fun processPenaltyTurn(state: GameState, turnState: TurnState): GameState {
        val currentPlayer = state.currentPlayer ?: return state
        val penaltyType = turnState.penaltyType ?: return state
        
        return when (penaltyType) {
            ScoringPosition.PIG_OUT -> {
                // Lose turn score, advance to next player
                advanceToNextPlayer(state.copy(currentTurnScore = 0, currentTurnState = TurnState()))
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
                        currentTurnScore = 0,
                        currentTurnState = TurnState()
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
                val newState = state.copy(
                    players = updatedPlayers, 
                    currentTurnScore = 0,
                    currentTurnState = TurnState()
                )
                advanceToNextPlayer(newState)
            }
            else -> state
        }
    }
    
    private fun bankPoints(state: GameState): GameState {
        if (!state.gameStarted || state.gameEnded || state.currentTurnScore == 0) return state
        if (!state.currentTurnState.isComplete) return state // Must complete both pig rolls
        
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
            currentTurnScore = 0,
            currentTurnState = TurnState()
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
            roundNumber = newRoundNumber,
            currentTurnState = TurnState()
        )
    }
    
    private fun undoLastRoll(state: GameState): GameState {
        val currentTurnState = state.currentTurnState
        return when {
            currentTurnState.pig2 != null -> {
                // Remove second pig roll
                state.copy(currentTurnState = currentTurnState.copy(pig2 = null, isComplete = false))
            }
            currentTurnState.pig1 != null -> {
                // Remove first pig roll and reset turn score
                state.copy(
                    currentTurnState = TurnState(),
                    currentTurnScore = 0
                )
            }
            else -> state // Nothing to undo
        }
    }
}