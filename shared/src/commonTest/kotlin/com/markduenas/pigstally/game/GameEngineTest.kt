package com.markduenas.pigstally.game

import com.markduenas.pigstally.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameEngineTest {
    
    private val gameEngine = GameEngine()
    
    private fun createTestGameState(): GameState {
        return GameState(
            players = listOf(
                Player(id = "player1", name = "Player 1", totalScore = 0),
                Player(id = "player2", name = "Player 2", totalScore = 0)
            ),
            gameStarted = true,
            currentPlayerIndex = 0
        )
    }
    
    @Test
    fun testNormalScoring() {
        var state = createTestGameState()
        
        // Roll first pig (Trotter)
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.TROTTER))
        assertEquals(5, state.currentTurnScore)
        assertEquals(ScoringPosition.TROTTER, state.currentTurnState.currentPig1?.position)
        assertEquals(1, state.currentTurnState.currentPigNumber) // Still need second pig
        
        // Roll second pig (Razorback)
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.RAZORBACK))
        assertEquals(10, state.currentTurnScore) // 5 + 5
        assertTrue(state.currentTurnState.currentPairComplete)
        assertTrue(state.currentTurnState.canRollAgain) // Should be able to roll again
        assertFalse(state.currentTurnState.hasPenalty)
    }
    
    @Test
    fun testDoubleBonusScoring() {
        var state = createTestGameState()
        
        // Roll double trotter (should get 20 points)
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.TROTTER))
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.TROTTER))
        
        assertEquals(20, state.currentTurnScore) // 5+5+10 bonus
        assertTrue(state.currentTurnState.canRollAgain)
    }
    
    @Test
    fun testSiderScoring() {
        var state = createTestGameState()
        
        // Both pigs same side (should get 1 point)
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.DOT_UP))
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.DOT_UP))
        
        assertEquals(1, state.currentTurnScore)
        assertTrue(state.currentTurnState.canRollAgain)
        assertFalse(state.currentTurnState.hasPenalty)
    }
    
    @Test
    fun testPigOutPenalty() {
        var state = createTestGameState()
        
        // Opposite sides = Pig Out
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.DOT_UP))
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.DOT_DOWN))
        
        // Should advance to next player and reset score
        assertEquals(0, state.currentTurnScore)
        assertEquals(1, state.currentPlayerIndex) // Advanced to player 2
        assertEquals(TurnState(), state.currentTurnState) // Reset turn state
    }
    
    @Test
    fun testImmediateOinkerPenalty() {
        var state = createTestGameState()
        val originalPlayer = state.players[0]
        
        // Set player to have some score first
        state = state.copy(
            players = state.players.map { 
                if (it.id == originalPlayer.id) it.copy(totalScore = 50) else it 
            }
        )
        
        // Roll Oinker (should immediately end turn and reset total score)
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.OINKER))
        
        // Should advance to next player, reset turn score, and reset total score to 0
        assertEquals(0, state.currentTurnScore)
        assertEquals(1, state.currentPlayerIndex) // Advanced to player 2
        assertEquals(0, state.players[0].totalScore) // Total score reset to 0
        assertEquals(TurnState(), state.currentTurnState) // Reset turn state
    }
    
    @Test
    fun testImmediatePiggybackPenalty() {
        var state = createTestGameState()
        
        // Roll Piggyback (should immediately eliminate player)
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.PIGGYBACK))
        
        // Should advance to next player and eliminate current player
        assertEquals(0, state.currentTurnScore)
        assertEquals(1, state.currentPlayerIndex) // Advanced to player 2
        assertTrue(state.players[0].isEliminated) // Player 1 eliminated
        assertEquals(TurnState(), state.currentTurnState) // Reset turn state
    }
    
    @Test
    fun testBankPoints() {
        var state = createTestGameState()
        
        // Score some points
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.TROTTER))
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.RAZORBACK))
        assertEquals(10, state.currentTurnScore)
        
        // Bank the points
        state = gameEngine.processAction(state, GameAction.BankPoints)
        
        // Should add to total score and advance to next player
        assertEquals(10, state.players[0].totalScore)
        assertEquals(0, state.currentTurnScore)
        assertEquals(1, state.currentPlayerIndex) // Advanced to player 2
        assertEquals(TurnState(), state.currentTurnState) // Reset turn state
    }
    
    @Test
    fun testRollAgain() {
        var state = createTestGameState()
        
        // Score first pair
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.TROTTER))
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.RAZORBACK))
        assertEquals(10, state.currentTurnScore)
        assertTrue(state.currentTurnState.canRollAgain)
        
        // Roll again
        state = gameEngine.processAction(state, GameAction.RollAgain)
        
        // Should move current pair to completed and reset for next pair
        assertEquals(1, state.currentTurnState.completedPairs.size)
        assertEquals(10, state.currentTurnScore) // Score maintained
        assertEquals(1, state.currentTurnState.currentPigNumber) // Ready for next pig 1
    }
    
    @Test
    fun testMultiplePairs() {
        var state = createTestGameState()
        
        // First pair: Trotter + Razorback = 10
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.TROTTER))
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.RAZORBACK))
        state = gameEngine.processAction(state, GameAction.RollAgain)
        
        // Second pair: Snouter + side = 10
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.SNOUTER))
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.DOT_UP))
        
        // Should have total of 20 points
        assertEquals(20, state.currentTurnScore)
        assertEquals(2, state.currentTurnState.completedPairs.size)
        assertTrue(state.currentTurnState.canRollAgain)
    }
    
    @Test
    fun testUndo() {
        var state = createTestGameState()
        
        // Roll first pig
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.TROTTER))
        assertEquals(5, state.currentTurnScore)
        
        // Undo
        state = gameEngine.processAction(state, GameAction.UndoLastRoll)
        assertEquals(0, state.currentTurnScore)
        assertEquals(TurnState(), state.currentTurnState)
        
        // Test undo with completed pairs
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.TROTTER))
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.RAZORBACK))
        state = gameEngine.processAction(state, GameAction.RollAgain)
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.SNOUTER))
        
        // Undo should remove the Snouter
        state = gameEngine.processAction(state, GameAction.UndoLastRoll)
        assertEquals(10, state.currentTurnScore) // Back to just first pair
        assertEquals(1, state.currentTurnState.currentPigNumber) // Ready for pig 1 again
    }
    
    @Test
    fun testWinCondition() {
        var state = createTestGameState()
        
        // Set player close to winning
        state = state.copy(
            players = state.players.map { 
                if (it.id == "player1") it.copy(totalScore = 95) else it 
            }
        )
        
        // Score enough to win
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.TROTTER))
        state = gameEngine.processAction(state, GameAction.ScorePig(ScoringPosition.RAZORBACK))
        state = gameEngine.processAction(state, GameAction.BankPoints)
        
        // Should end the game
        assertTrue(state.gameEnded)
        assertEquals("player1", state.winner?.id)
        assertEquals(105, state.winner?.totalScore)
    }
}