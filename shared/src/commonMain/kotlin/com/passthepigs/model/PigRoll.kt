package com.passthepigs.model

data class PigRoll(
    val position: ScoringPosition,
    val points: Int = position.points
)

data class TurnState(
    val pig1: PigRoll? = null,
    val pig2: PigRoll? = null,
    val isComplete: Boolean = false
) {
    val currentPigNumber: Int
        get() = when {
            pig1 == null -> 1
            pig2 == null -> 2
            else -> 0 // Turn complete
        }
    
    val totalPoints: Int
        get() = (pig1?.points ?: 0) + (pig2?.points ?: 0)
    
    val hasPenalty: Boolean
        get() = pig1?.position?.isPenalty == true || pig2?.position?.isPenalty == true || 
                (pig1?.position == ScoringPosition.SIDER && pig2?.position == ScoringPosition.SIDER)
    
    val penaltyType: ScoringPosition?
        get() = when {
            // Check for explicit penalty positions first
            pig1?.position?.isPenalty == true -> pig1.position
            pig2?.position?.isPenalty == true -> pig2.position
            // Check for pig out (both siders)
            pig1?.position == ScoringPosition.SIDER && pig2?.position == ScoringPosition.SIDER -> ScoringPosition.PIG_OUT
            else -> null
        }
    
    private fun arePigsTouching(): Boolean {
        // For now, we'll consider OINKER as a special case that needs to be explicitly selected
        return pig1?.position == ScoringPosition.OINKER || pig2?.position == ScoringPosition.OINKER
    }
    
    private fun isPiggyback(): Boolean {
        // For now, we'll consider PIGGYBACK as a special case that needs to be explicitly selected
        return pig1?.position == ScoringPosition.PIGGYBACK || pig2?.position == ScoringPosition.PIGGYBACK
    }
    
    fun getDoubleBonus(): Int {
        return if (pig1?.position == pig2?.position && pig1?.position?.isPositive == true) {
            when (pig1.position) {
                ScoringPosition.TROTTER -> 10 // 20 total (5+5+10 bonus)
                ScoringPosition.SNOUTER -> 20 // 40 total (10+10+20 bonus)
                ScoringPosition.LEANING_JOWLER -> 30 // 60 total (15+15+30 bonus)
                else -> 0
            }
        } else 0
    }
    
    val finalPoints: Int
        get() = if (hasPenalty) 0 else totalPoints + getDoubleBonus()
}