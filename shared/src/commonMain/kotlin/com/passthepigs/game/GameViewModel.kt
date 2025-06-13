package com.passthepigs.game

import com.passthepigs.model.*
import com.passthepigs.storage.GameStateStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel(
    private val storage: GameStateStorage? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val gameEngine = GameEngine()
    
    private val _gameState = MutableStateFlow(
        GameState(
            players = listOf(
                Player.createDefault(1),
                Player.createDefault(2)
            )
        )
    )
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    init {
        loadGameState()
    }
    
    fun processAction(action: GameAction) {
        val currentState = _gameState.value
        val newState = gameEngine.processAction(currentState, action)
        _gameState.value = newState
        saveGameState()
    }
    
    private fun saveGameState() {
        storage?.let { storage ->
            scope.launch {
                try {
                    storage.saveGameState(_gameState.value)
                } catch (e: Exception) {
                    // Ignore save errors
                }
            }
        }
    }
    
    private fun loadGameState() {
        storage?.let { storage ->
            scope.launch {
                try {
                    val savedState = storage.loadGameState()
                    savedState?.let { 
                        _gameState.value = it
                    }
                } catch (e: Exception) {
                    // Ignore load errors
                }
            }
        }
    }
    
    fun addPlayer(name: String = "") {
        processAction(GameAction.AddPlayer(name))
    }
    
    fun removePlayer(playerId: String) {
        processAction(GameAction.RemovePlayer(playerId))
    }
    
    fun updatePlayerName(playerId: String, newName: String) {
        processAction(GameAction.UpdatePlayerName(playerId, newName))
    }
    
    fun startGame() {
        processAction(GameAction.StartGame)
    }
    
    fun newGame() {
        processAction(GameAction.NewGame)
    }
    
    fun scorePosition(position: ScoringPosition, customPoints: Int = 0) {
        processAction(GameAction.Score(position, customPoints))
    }
    
    fun bankPoints() {
        processAction(GameAction.BankPoints)
    }
    
    fun undoLastRoll() {
        processAction(GameAction.UndoLastRoll)
    }
}