package com.passthepigs.model

sealed class GameAction {
    data class AddPlayer(val name: String) : GameAction()
    data class RemovePlayer(val playerId: String) : GameAction()
    data class UpdatePlayerName(val playerId: String, val newName: String) : GameAction()
    data class ReorderPlayers(val newOrder: List<Player>) : GameAction()
    
    object StartGame : GameAction()
    object NewGame : GameAction()
    
    data class Score(val position: ScoringPosition, val customPoints: Int = 0) : GameAction()
    object BankPoints : GameAction()
    object UndoLastRoll : GameAction()
}