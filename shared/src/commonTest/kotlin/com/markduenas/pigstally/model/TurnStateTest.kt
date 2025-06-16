package com.markduenas.pigstally.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TurnStateTest {
    
    @Test
    fun testEmptyTurnState() {
        val turnState = TurnState()
        
        assertEquals(1, turnState.currentPigNumber)
        assertFalse(turnState.hasCurrentPair)
        assertFalse(turnState.currentPairComplete)
        assertEquals(0, turnState.totalTurnPoints)
        assertFalse(turnState.hasPenalty)
        assertFalse(turnState.canRollAgain)
        assertTrue(turnState.allRolls.isEmpty())
    }
    
    @Test
    fun testSinglePigRoll() {
        val turnState = TurnState(
            currentPig1 = PigRoll(ScoringPosition.TROTTER)
        )
        
        assertEquals(2, turnState.currentPigNumber) // Need pig 2
        assertFalse(turnState.hasCurrentPair)
        assertFalse(turnState.currentPairComplete)
        assertEquals(5, turnState.totalTurnPoints)
        assertFalse(turnState.hasPenalty)
        assertFalse(turnState.canRollAgain)
        assertEquals(1, turnState.allRolls.size)
    }
    
    @Test
    fun testCompletePairNoPenalty() {
        val turnState = TurnState(
            currentPig1 = PigRoll(ScoringPosition.TROTTER),
            currentPig2 = PigRoll(ScoringPosition.RAZORBACK)
        )
        
        assertEquals(0, turnState.currentPigNumber) // Pair complete
        assertTrue(turnState.hasCurrentPair)
        assertTrue(turnState.currentPairComplete)
        assertEquals(10, turnState.totalTurnPoints) // 5 + 5
        assertFalse(turnState.hasPenalty)
        assertTrue(turnState.canRollAgain) // Can roll again since no penalty
        assertEquals(2, turnState.allRolls.size)
    }
    
    @Test
    fun testCompletePairWithPenalty() {
        val turnState = TurnState(
            currentPig1 = PigRoll(ScoringPosition.DOT_UP),
            currentPig2 = PigRoll(ScoringPosition.DOT_DOWN)
        )
        
        assertTrue(turnState.hasCurrentPair)
        assertTrue(turnState.currentPairComplete)
        assertEquals(0, turnState.totalTurnPoints) // Pig Out = 0 points
        assertTrue(turnState.hasPenalty)
        assertEquals(ScoringPosition.PIG_OUT, turnState.penaltyType)
        assertFalse(turnState.canRollAgain) // Cannot roll again due to penalty
    }
    
    @Test
    fun testSiderScoring() {
        // Same sides = 1 point
        val siderTurnState = TurnState(
            currentPig1 = PigRoll(ScoringPosition.DOT_UP),
            currentPig2 = PigRoll(ScoringPosition.DOT_UP)
        )
        
        assertEquals(1, siderTurnState.totalTurnPoints)
        assertFalse(siderTurnState.hasPenalty)
        assertTrue(siderTurnState.canRollAgain)
    }
    
    @Test
    fun testDoubleBonus() {
        val doubleTrotter = TurnState(
            currentPig1 = PigRoll(ScoringPosition.TROTTER),
            currentPig2 = PigRoll(ScoringPosition.TROTTER)
        )
        
        assertEquals(20, doubleTrotter.totalTurnPoints) // 5+5+10 bonus
        assertFalse(doubleTrotter.hasPenalty)
        assertTrue(doubleTrotter.canRollAgain)
    }
    
    @Test
    fun testAddCurrentPairToCompleted() {
        val turnState = TurnState(
            currentPig1 = PigRoll(ScoringPosition.TROTTER),
            currentPig2 = PigRoll(ScoringPosition.RAZORBACK)
        )
        
        val newTurnState = turnState.addCurrentPairToCompleted()
        
        assertEquals(1, newTurnState.completedPairs.size)
        assertEquals(10, newTurnState.completedPairs[0].finalPoints)
        assertEquals(null, newTurnState.currentPig1)
        assertEquals(null, newTurnState.currentPig2)
        assertEquals(10, newTurnState.totalTurnPoints) // Points from completed pair
        assertEquals(1, newTurnState.currentPigNumber) // Ready for next pig 1
    }
    
    @Test
    fun testMultipleCompletedPairs() {
        val pair1 = PigPair(
            PigRoll(ScoringPosition.TROTTER),
            PigRoll(ScoringPosition.RAZORBACK)
        )
        val pair2 = PigPair(
            PigRoll(ScoringPosition.SNOUTER),
            PigRoll(ScoringPosition.DOT_UP)
        )
        
        val turnState = TurnState(
            completedPairs = listOf(pair1, pair2),
            currentPig1 = PigRoll(ScoringPosition.LEANING_JOWLER)
        )
        
        assertEquals(35, turnState.totalTurnPoints) // 10 + 10 + 15
        assertEquals(3, turnState.allRolls.size) // 2 pairs + 1 current
        assertEquals(2, turnState.currentPigNumber) // Need pig 2
    }
    
    @Test
    fun testCanRollAgainLogic() {
        // Can roll again: has current pair, no penalty
        val canRoll = TurnState(
            currentPig1 = PigRoll(ScoringPosition.TROTTER),
            currentPig2 = PigRoll(ScoringPosition.RAZORBACK)
        )
        assertTrue(canRoll.canRollAgain)
        
        // Cannot roll: has penalty
        val hasPenalty = TurnState(
            currentPig1 = PigRoll(ScoringPosition.DOT_UP),
            currentPig2 = PigRoll(ScoringPosition.DOT_DOWN)
        )
        assertFalse(hasPenalty.canRollAgain)
        
        // Cannot roll: incomplete pair
        val incomplete = TurnState(
            currentPig1 = PigRoll(ScoringPosition.TROTTER)
        )
        assertFalse(incomplete.canRollAgain)
        
        // Cannot roll: no pigs yet
        val empty = TurnState()
        assertFalse(empty.canRollAgain)
    }
    
    @Test
    fun testAllRollsTracking() {
        val pair1 = PigPair(
            PigRoll(ScoringPosition.TROTTER),
            PigRoll(ScoringPosition.RAZORBACK)
        )
        
        val turnState = TurnState(
            completedPairs = listOf(pair1),
            currentPig1 = PigRoll(ScoringPosition.SNOUTER),
            currentPig2 = PigRoll(ScoringPosition.DOT_UP)
        )
        
        val allRolls = turnState.allRolls
        assertEquals(4, allRolls.size)
        assertEquals(ScoringPosition.TROTTER, allRolls[0].position)
        assertEquals(ScoringPosition.RAZORBACK, allRolls[1].position)
        assertEquals(ScoringPosition.SNOUTER, allRolls[2].position)
        assertEquals(ScoringPosition.DOT_UP, allRolls[3].position)
    }
    
    @Test
    fun testPenaltyTypes() {
        // Test Oinker penalty
        val oinkerState = TurnState(
            currentPig1 = PigRoll(ScoringPosition.OINKER),
            currentPig2 = PigRoll(ScoringPosition.TROTTER)
        )
        assertTrue(oinkerState.hasPenalty)
        assertEquals(ScoringPosition.OINKER, oinkerState.penaltyType)
        
        // Test Piggyback penalty
        val piggybackState = TurnState(
            currentPig1 = PigRoll(ScoringPosition.TROTTER),
            currentPig2 = PigRoll(ScoringPosition.PIGGYBACK)
        )
        assertTrue(piggybackState.hasPenalty)
        assertEquals(ScoringPosition.PIGGYBACK, piggybackState.penaltyType)
        
        // Test Pig Out penalty
        val pigOutState = TurnState(
            currentPig1 = PigRoll(ScoringPosition.DOT_UP),
            currentPig2 = PigRoll(ScoringPosition.DOT_DOWN)
        )
        assertTrue(pigOutState.hasPenalty)
        assertEquals(ScoringPosition.PIG_OUT, pigOutState.penaltyType)
    }
}