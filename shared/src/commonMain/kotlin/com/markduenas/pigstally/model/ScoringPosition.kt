package com.markduenas.pigstally.model

enum class ScoringPosition(val displayName: String, val points: Int, val isPenalty: Boolean = false) {
    // Individual pig positions (for input)
    DOT_UP("Dot Sider", 0),      // Pig on side with dot facing up
    DOT_DOWN("Sider", 0),  // Pig on side with dot facing down
    TROTTER("Trotter", 5),
    RAZORBACK("Razorback", 5),
    SNOUTER("Snouter", 10),
    LEANING_JOWLER("Leaning Jowler", 15),
    
    // Combined results (calculated)
    SIDER("Sider", 1),        // Both pigs same side (both dot up OR both dot down)
    PIG_OUT("Pig Out", 0, true), // Pigs opposite sides (one dot up, one dot down)
    
    // Penalty positions
    OINKER("Oinker", 0, true),
    PIGGYBACK("Piggyback", 0, true);
    
    val isPositive: Boolean
        get() = points > 0 && !isPenalty
    
    val isSide: Boolean
        get() = this == DOT_UP || this == DOT_DOWN
}