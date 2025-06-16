package com.markduenas.pigstally.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PigPairTest {
    
    @Test
    fun testSiderSameSide() {
        // Both pigs dot up = 1 point
        val pair1 = PigPair(
            PigRoll(ScoringPosition.DOT_UP),
            PigRoll(ScoringPosition.DOT_UP)
        )
        assertEquals(1, pair1.totalPoints)
        assertEquals(1, pair1.finalPoints)
        assertFalse(pair1.hasPenalty)
        
        // Both pigs dot down = 1 point
        val pair2 = PigPair(
            PigRoll(ScoringPosition.DOT_DOWN),
            PigRoll(ScoringPosition.DOT_DOWN)
        )
        assertEquals(1, pair2.totalPoints)
        assertEquals(1, pair2.finalPoints)
        assertFalse(pair2.hasPenalty)
    }
    
    @Test
    fun testPigOutOppositeSides() {
        // One dot up, one dot down = Pig Out
        val pair = PigPair(
            PigRoll(ScoringPosition.DOT_UP),
            PigRoll(ScoringPosition.DOT_DOWN)
        )
        assertEquals(0, pair.totalPoints)
        assertEquals(0, pair.finalPoints)
        assertTrue(pair.hasPenalty)
        assertEquals(ScoringPosition.PIG_OUT, pair.penaltyType)
    }
    
    @Test
    fun testMixedCombinations() {
        // One side + one other position = normal scoring
        val pair1 = PigPair(
            PigRoll(ScoringPosition.DOT_UP),
            PigRoll(ScoringPosition.TROTTER)
        )
        assertEquals(5, pair1.totalPoints) // 0 + 5
        assertEquals(5, pair1.finalPoints)
        assertFalse(pair1.hasPenalty)
        
        // Snouter + side
        val pair2 = PigPair(
            PigRoll(ScoringPosition.SNOUTER),
            PigRoll(ScoringPosition.DOT_DOWN)
        )
        assertEquals(10, pair2.totalPoints) // 10 + 0
        assertEquals(10, pair2.finalPoints)
        assertFalse(pair2.hasPenalty)
    }
    
    @Test
    fun testDoubleBonuses() {
        // Double Trotter = 20 points (5+5+10 bonus)
        val doubleTrotter = PigPair(
            PigRoll(ScoringPosition.TROTTER),
            PigRoll(ScoringPosition.TROTTER)
        )
        assertEquals(10, doubleTrotter.totalPoints) // 5 + 5
        assertEquals(10, doubleTrotter.getDoubleBonus())
        assertEquals(20, doubleTrotter.finalPoints) // 10 + 10 bonus
        
        // Double Razorback = 20 points (5+5+10 bonus)
        val doubleRazorback = PigPair(
            PigRoll(ScoringPosition.RAZORBACK),
            PigRoll(ScoringPosition.RAZORBACK)
        )
        assertEquals(10, doubleRazorback.totalPoints)
        assertEquals(10, doubleRazorback.getDoubleBonus())
        assertEquals(20, doubleRazorback.finalPoints)
        
        // Double Snouter = 40 points (10+10+20 bonus)
        val doubleSnouter = PigPair(
            PigRoll(ScoringPosition.SNOUTER),
            PigRoll(ScoringPosition.SNOUTER)
        )
        assertEquals(20, doubleSnouter.totalPoints) // 10 + 10
        assertEquals(20, doubleSnouter.getDoubleBonus())
        assertEquals(40, doubleSnouter.finalPoints) // 20 + 20 bonus
        
        // Double Leaning Jowler = 60 points (15+15+30 bonus)
        val doubleJowler = PigPair(
            PigRoll(ScoringPosition.LEANING_JOWLER),
            PigRoll(ScoringPosition.LEANING_JOWLER)
        )
        assertEquals(30, doubleJowler.totalPoints) // 15 + 15
        assertEquals(30, doubleJowler.getDoubleBonus())
        assertEquals(60, doubleJowler.finalPoints) // 30 + 30 bonus
    }
    
    @Test
    fun testMixedCombinationsNoBonus() {
        // Mixed combinations get no bonus
        val mixed = PigPair(
            PigRoll(ScoringPosition.TROTTER),
            PigRoll(ScoringPosition.RAZORBACK)
        )
        assertEquals(10, mixed.totalPoints) // 5 + 5
        assertEquals(0, mixed.getDoubleBonus()) // No bonus for mixed
        assertEquals(10, mixed.finalPoints)
        assertFalse(mixed.hasPenalty)
    }
    
    @Test
    fun testImmediatePenalties() {
        // Oinker penalty
        val oinkerPair = PigPair(
            PigRoll(ScoringPosition.OINKER),
            PigRoll(ScoringPosition.TROTTER)
        )
        assertTrue(oinkerPair.hasPenalty)
        assertEquals(ScoringPosition.OINKER, oinkerPair.penaltyType)
        assertEquals(0, oinkerPair.finalPoints)
        
        // Piggyback penalty
        val piggybackPair = PigPair(
            PigRoll(ScoringPosition.PIGGYBACK),
            PigRoll(ScoringPosition.SNOUTER)
        )
        assertTrue(piggybackPair.hasPenalty)
        assertEquals(ScoringPosition.PIGGYBACK, piggybackPair.penaltyType)
        assertEquals(0, piggybackPair.finalPoints)
    }
}