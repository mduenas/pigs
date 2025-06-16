package com.markduenas.pigstally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markduenas.pigstally.model.Player
import com.markduenas.pigstally.ui.theme.PassThePigsColors

@Composable
fun PlayerCard(
    player: Player,
    playerIndex: Int,
    isCurrentPlayer: Boolean = false,
    currentTurnScore: Int = 0,
    modifier: Modifier = Modifier
) {
    val playerColor = PassThePigsColors.getPlayerColor(playerIndex)
    val cardColor = if (isCurrentPlayer) {
        playerColor.copy(alpha = 0.2f)
    } else {
        Color.White
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentPlayer) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Player name
            Text(
                text = player.name,
                fontSize = 18.sp,
                fontWeight = if (isCurrentPlayer) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrentPlayer) playerColor else PassThePigsColors.OnSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Total score
            Text(
                text = "${player.totalScore}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = playerColor
            )
            
            // Progress bar
            LinearProgressIndicator(
                progress = player.progressPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = playerColor,
                trackColor = playerColor.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Turn score (if current player)
//            if (isCurrentPlayer && currentTurnScore > 0) {
//                Text(
//                    text = "Turn: +$currentTurnScore",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium,
//                    color = PassThePigsColors.PositiveScore
//                )
//            }
            
            // Status indicators
            when {
                player.isEliminated -> {
                    Text(
                        text = "ELIMINATED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PassThePigsColors.NegativeScore
                    )
                }
                player.isWinner -> {
                    Text(
                        text = "WINNER!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PassThePigsColors.PositiveScore
                    )
                }
                isCurrentPlayer -> {
                    Text(
                        text = "YOUR TURN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = playerColor
                    )
                }
            }
        }
    }
}