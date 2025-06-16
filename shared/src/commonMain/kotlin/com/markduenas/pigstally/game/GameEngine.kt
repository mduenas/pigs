package com.markduenas.pigstally.game

import com.markduenas.pigstally.model.*

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
            GameAction.RollAgain -> rollAgain(currentState)
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
        
        val currentTurnState = state.currentTurnState
        val pigRoll = PigRoll(position)
        
        val newTurnState = when (currentTurnState.currentPigNumber) {
            1 -> currentTurnState.copy(currentPig1 = pigRoll)
            2 -> currentTurnState.copy(currentPig2 = pigRoll)
            else -> currentTurnState // Should not happen
        }
        
        val newState = state.copy(
            currentTurnState = newTurnState,
            currentTurnScore = newTurnState.totalTurnPoints
        )
        
        // Check for immediate penalties (Oinker, Piggyback) that end turn right away
        if (position.isPenalty && position != ScoringPosition.PIG_OUT) {
            return processImmediatePenalty(newState, position)
        }
        
        // If current pair is complete, check for other penalties
        return if (newTurnState.currentPairComplete) {
            if (newTurnState.hasPenalty) {
                processPenaltyTurn(newState, newTurnState)
            } else {
                newState // Player can choose to bank or roll again
            }
        } else {
            newState
        }
    }
    
    fun rollAgain(state: GameState): GameState {
        if (!state.gameStarted || state.gameEnded) return state
        val turnState = state.currentTurnState
        if (!turnState.canRollAgain) return state
        
        // Move current pair to completed pairs and reset for next roll
        val newTurnState = turnState.addCurrentPairToCompleted()
        return state.copy(
            currentTurnState = newTurnState,
            currentTurnScore = newTurnState.totalTurnPoints
        )
    }
    
    private fun processImmediatePenalty(state: GameState, penaltyType: ScoringPosition): GameState {
        val currentPlayer = state.currentPlayer ?: return state
        
        return when (penaltyType) {
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
        
        val turnState = state.currentTurnState
        if (!turnState.currentPairComplete && turnState.completedPairs.isEmpty()) return state
        
        val currentPlayer = state.currentPlayer ?: return state
        
        // Add current pair to completed if exists
        val finalTurnState = if (turnState.currentPairComplete && !turnState.hasPenalty) {
            turnState.addCurrentPairToCompleted()
        } else {
            turnState
        }
        
        val finalTurnScore = finalTurnState.totalTurnPoints
        val newTotalScore = currentPlayer.totalScore + finalTurnScore
        
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
        val winner = updatedPlayers.find { it.totalScore >= state.winningScore && !it.isEliminated }
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
            currentTurnState.currentPig2 != null -> {
                // Remove second pig roll
                val newTurnState = currentTurnState.copy(currentPig2 = null)
                state.copy(
                    currentTurnState = newTurnState,
                    currentTurnScore = newTurnState.totalTurnPoints
                )
            }
            currentTurnState.currentPig1 != null -> {
                // Remove first pig roll
                val newTurnState = currentTurnState.copy(currentPig1 = null)
                state.copy(
                    currentTurnState = newTurnState,
                    currentTurnScore = newTurnState.totalTurnPoints
                )
            }
            currentTurnState.completedPairs.isNotEmpty() -> {
                // Remove last completed pair
                val newTurnState = currentTurnState.copy(
                    completedPairs = currentTurnState.completedPairs.dropLast(1)
                )
                state.copy(
                    currentTurnState = newTurnState,
                    currentTurnScore = newTurnState.totalTurnPoints
                )
            }
            else -> state // Nothing to undo
        }
    }
}