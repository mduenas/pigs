package com.passthepigs.model

enum class ScoringPosition(val displayName: String, val points: Int, val isPenalty: Boolean = false) {
    // Single pig positions
    DOT("Dot Sider", 1),
    SIDER("Sider", 0),
    TROTTER("Trotter", 5),
    RAZORBACK("Razorback", 5),
    SNOUTER("Snouter", 10),
    LEANING_JOWLER("Leaning Jowler", 15),
    
    // Penalty positions
    PIG_OUT("Pig Out", 0, true),
    OINKER("Oinker", 0, true),
    PIGGYBACK("Piggyback", 0, true);
    
    val isPositive: Boolean
        get() = points > 0 && !isPenalty
}