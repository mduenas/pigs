package com.passthepigs.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.passthepigs.model.ScoringPosition
import com.passthepigs.ui.theme.PassThePigsColors

@Composable
fun ScoringButton(
    position: ScoringPosition,
    onScore: (ScoringPosition) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val buttonColor = when {
        position.isPenalty -> PassThePigsColors.NegativeScore
        position.points > 0 -> PassThePigsColors.PositiveScore
        else -> PassThePigsColors.SpecialScore
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.2f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = buttonColor.copy(alpha = if (enabled) 1f else 0.5f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (enabled) 4.dp else 2.dp
        )
    ) {
        Button(
            onClick = { onScore(position) },
            enabled = enabled,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = position.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                if (position.points != 0 || position.isPenalty) {
                    Text(
                        text = when {
                            position.isPenalty -> when (position) {
                                ScoringPosition.PIG_OUT -> "Lose Turn"
                                ScoringPosition.OINKER -> "Score → 0"
                                ScoringPosition.PIGGYBACK -> "Eliminated"
                                else -> "Penalty"
                            }
                            else -> "${position.points} pts"
                        },
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}