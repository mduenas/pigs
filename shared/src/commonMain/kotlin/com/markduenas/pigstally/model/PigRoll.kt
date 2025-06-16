package com.markduenas.pigstally.model

data class PigRoll(
    val position: ScoringPosition,
    val points: Int = position.points
)

data class PigPair(
    val pig1: PigRoll,
    val pig2: PigRoll
) {
    val totalPoints: Int
        get() {
            // Handle sider combinations specially
            return when {
                isSiderCombination() -> siderPoints()
                else -> pig1.points + pig2.points
            }
        }
    
    val hasPenalty: Boolean
        get() = pig1.position.isPenalty || pig2.position.isPenalty || isPigOut()
    
    val penaltyType: ScoringPosition?
        get() = when {
            pig1.position.isPenalty -> pig1.position
            pig2.position.isPenalty -> pig2.position
            isPigOut() -> ScoringPosition.PIG_OUT
            else -> null
        }
    
    private fun isSiderCombination(): Boolean {
        return pig1.position.isSide && pig2.position.isSide
    }
    
    private fun isPigOut(): Boolean {
        // Pig Out when pigs land on opposite sides (one dot up, one dot down)
        return (pig1.position == ScoringPosition.DOT_UP && pig2.position == ScoringPosition.DOT_DOWN) ||
               (pig1.position == ScoringPosition.DOT_DOWN && pig2.position == ScoringPosition.DOT_UP)
    }
    
    private fun siderPoints(): Int {
        return when {
            isPigOut() -> 0 // Opposite sides = Pig Out
            // Same sides (both dot up OR both dot down) = 1 point
            (pig1.position == ScoringPosition.DOT_UP && pig2.position == ScoringPosition.DOT_UP) ||
            (pig1.position == ScoringPosition.DOT_DOWN && pig2.position == ScoringPosition.DOT_DOWN) -> 1
            else -> 0
        }
    }
    
    fun getDoubleBonus(): Int {
        return if (pig1.position == pig2.position && pig1.position.isPositive) {
            when (pig1.position) {
                ScoringPosition.TROTTER -> 10 // 20 total (5+5+10 bonus)
                ScoringPosition.RAZORBACK -> 10 // 20 total (5+5+10 bonus)
                ScoringPosition.SNOUTER -> 20 // 40 total (10+10+20 bonus)
                ScoringPosition.LEANING_JOWLER -> 30 // 60 total (15+15+30 bonus)
                else -> 0
            }
        } else 0
    }
    
    val finalPoints: Int
        get() = if (hasPenalty) 0 else totalPoints + getDoubleBonus()
}

data class TurnState(
    val completedPairs: List<PigPair> = emptyList(),
    val currentPig1: PigRoll? = null,
    val currentPig2: PigRoll? = null,
    val canContinueRolling: Boolean = true
) {
    val currentPigNumber: Int
        get() = when {
            currentPig1 == null -> 1
            currentPig2 == null -> 2
            else -> 0 // Current pair complete
        }
    
    val hasCurrentPair: Boolean
        get() = currentPig1 != null && currentPig2 != null
    
    val currentPairComplete: Boolean
        get() = hasCurrentPair
    
    val totalTurnPoints: Int
        get() {
            val completedPoints = completedPairs.sumOf { it.finalPoints }
            val currentPoints = if (hasCurrentPair) {
                val currentPair = PigPair(currentPig1!!, currentPig2!!)
                currentPair.finalPoints
            } else {
                (currentPig1?.points ?: 0) + (currentPig2?.points ?: 0)
            }
            return completedPoints + currentPoints
        }
    
    val hasPenalty: Boolean
        get() = hasCurrentPair && PigPair(currentPig1!!, currentPig2!!).hasPenalty
    
    val penaltyType: ScoringPosition?
        get() = if (hasCurrentPair) PigPair(currentPig1!!, currentPig2!!).penaltyType else null
    
    val canRollAgain: Boolean
        get() = hasCurrentPair && !hasPenalty && canContinueRolling
    
    fun addCurrentPairToCompleted(): TurnState {
        return if (hasCurrentPair) {
            val newPair = PigPair(currentPig1!!, currentPig2!!)
            copy(
                completedPairs = completedPairs + newPair,
                currentPig1 = null,
                currentPig2 = null
            )
        } else {
            this
        }
    }
    
    val allRolls: List<PigRoll>
        get() {
            val completed = completedPairs.flatMap { listOf(it.pig1, it.pig2) }
            val current = listOfNotNull(currentPig1, currentPig2)
            return completed + current
        }
}