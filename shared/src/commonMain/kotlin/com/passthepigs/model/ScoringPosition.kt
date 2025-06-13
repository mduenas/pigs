package com.passthepigs.model

enum class ScoringPosition(val displayName: String, val points: Int, val isPenalty: Boolean = false) {
    // Single pig positions
    SIDER("Sider", 0),
    TROTTER("Trotter", 5),
    RAZORBACK("Razorback", 5),
    SNOUTER("Snouter", 10),
    LEANING_JOWLER("Leaning Jowler", 15),
    
    // Double positions
    DOUBLE_TROTTER("Double Trotter", 20),
    DOUBLE_SNOUTER("Double Snouter", 40),
    DOUBLE_LEANING_JOWLER("Double Leaning Jowler", 60),
    
    // Mixed combinations (calculated dynamically)
    MIXED("Mixed", 0),
    
    // Penalty positions
    PIG_OUT("Pig Out", 0, true),
    OINKER("Oinker", 0, true),
    PIGGYBACK("Piggyback", 0, true);
    
    val isDouble: Boolean
        get() = name.startsWith("DOUBLE_")
    
    val isPositive: Boolean
        get() = points > 0 && !isPenalty
}