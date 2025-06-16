package com.markduenas.pigstally.game

import com.markduenas.pigstally.model.*
import com.markduenas.pigstally.storage.GameStateStorage
import com.markduenas.pigstally.storage.PreferencesStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel(
    private val storage: GameStateStorage? = null,
    private val preferencesStorage: PreferencesStorage? = null,
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
    
    private val _preferences = MutableStateFlow(GamePreferences.DEFAULT)
    val preferences: StateFlow<GamePreferences> = _preferences.asStateFlow()
    
    init {
        loadGameState()
        loadPreferences()
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
    
    private fun loadPreferences() {
        preferencesStorage?.let { storage ->
            scope.launch {
                try {
                    val savedPreferences = storage.loadPreferences()
                    _preferences.value = savedPreferences
                } catch (e: Exception) {
                    // Ignore load errors, use defaults
                }
            }
        }
    }
    
    private fun savePreferences() {
        preferencesStorage?.let { storage ->
            scope.launch {
                try {
                    storage.savePreferences(_preferences.value)
                } catch (e: Exception) {
                    // Ignore save errors
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
        val prefs = _preferences.value
        val players = (1..prefs.defaultPlayerCount).map { Player.createDefault(it) }
        _gameState.value = GameState(
            players = players,
            winningScore = prefs.winningScore
        )
        saveGameState()
    }
    
    fun updatePreferences(newPreferences: GamePreferences) {
        _preferences.value = newPreferences
        savePreferences()
    }
    
    fun scorePig(position: ScoringPosition) {
        processAction(GameAction.ScorePig(position))
    }
    
    fun bankPoints() {
        processAction(GameAction.BankPoints)
    }
    
    fun rollAgain() {
        processAction(GameAction.RollAgain)
    }
    
    fun undoLastRoll() {
        processAction(GameAction.UndoLastRoll)
    }
}