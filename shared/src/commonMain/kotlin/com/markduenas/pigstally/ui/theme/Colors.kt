package com.markduenas.pigstally.ui.theme

import androidx.compose.ui.graphics.Color

object PassThePigsColors {
    val Primary = Color(0xFFD84315) // Orange-red for pig theme
    val PrimaryVariant = Color(0xFFBF360C)
    val Secondary = Color(0xFF8BC34A) // Green for positive actions
    val SecondaryVariant = Color(0xFF689F38)
    
    val Background = Color(0xFFFFF3E0) // Light cream
    val Surface = Color(0xFFFFFFFF)
    
    val OnPrimary = Color.White
    val OnSecondary = Color.Black
    val OnBackground = Color(0xFF3E2723)
    val OnSurface = Color(0xFF3E2723)
    
    // Scoring colors
    val PositiveScore = Color(0xFF4CAF50) // Green
    val NegativeScore = Color(0xFFF44336) // Red
    val NeutralScore = Color(0xFF9E9E9E) // Gray
    val SpecialScore = Color(0xFFFFC107) // Amber
    
    // Player colors
    val Player1 = Color(0xFF2196F3) // Blue
    val Player2 = Color(0xFFFF9800) // Orange
    val Player3 = Color(0xFF9C27B0) // Purple
    val Player4 = Color(0xFF009688) // Teal
    val Player5 = Color(0xFFE91E63) // Pink
    val Player6 = Color(0xFF795548) // Brown
    
    fun getPlayerColor(index: Int): Color {
        return when (index % 6) {
            0 -> Player1
            1 -> Player2
            2 -> Player3
            3 -> Player4
            4 -> Player5
            5 -> Player6
            else -> Player1
        }
    }
}