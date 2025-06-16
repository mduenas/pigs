package com.markduenas.pigstally.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markduenas.pigstally.model.GamePreferences
import com.markduenas.pigstally.ui.theme.PassThePigsColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferences: GamePreferences,
    onPreferencesChange: (GamePreferences) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var defaultPlayerCount by remember(preferences) { mutableStateOf(preferences.defaultPlayerCount) }
    var winningScore by remember(preferences) { mutableStateOf(preferences.winningScore) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = PassThePigsColors.Primary
                )
            }
            
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PassThePigsColors.Primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Default Player Count Setting
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PassThePigsColors.Primary.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Default Number of Players",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PassThePigsColors.Primary
                )
                
                Text(
                    text = "Set how many players to start with by default",
                    fontSize = 14.sp,
                    color = PassThePigsColors.OnBackground
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Players:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = PassThePigsColors.OnBackground
                    )
                    
                    (2..6).forEach { count ->
                        FilterChip(
                            onClick = { 
                                defaultPlayerCount = count
                                onPreferencesChange(
                                    preferences.copy(defaultPlayerCount = count)
                                )
                            },
                            label = { Text(count.toString()) },
                            selected = defaultPlayerCount == count,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PassThePigsColors.Primary,
                                selectedLabelColor = PassThePigsColors.OnPrimary
                            )
                        )
                    }
                }
            }
        }
        
        // Winning Score Setting
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PassThePigsColors.Primary.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Winning Score",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PassThePigsColors.Primary
                )
                
                Text(
                    text = "Set the score needed to win the game",
                    fontSize = 14.sp,
                    color = PassThePigsColors.OnBackground
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Preset winning scores
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Presets:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = PassThePigsColors.OnBackground
                        )
                        
                        listOf(50, 100, 150, 200).forEach { score ->
                            FilterChip(
                                onClick = { 
                                    winningScore = score
                                    onPreferencesChange(
                                        preferences.copy(winningScore = score)
                                    )
                                },
                                label = { Text(score.toString()) },
                                selected = winningScore == score,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PassThePigsColors.Primary,
                                    selectedLabelColor = PassThePigsColors.OnPrimary
                                )
                            )
                        }
                    }
                    
                    // Custom score slider
                    Column {
                        Text(
                            text = "Custom: $winningScore points",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PassThePigsColors.OnBackground
                        )
                        
                        Slider(
                            value = winningScore.toFloat(),
                            onValueChange = { 
                                winningScore = it.toInt()
                            },
                            onValueChangeFinished = {
                                onPreferencesChange(
                                    preferences.copy(winningScore = winningScore)
                                )
                            },
                            valueRange = 50f..500f,
                            steps = 17, // 50, 75, 100, 125, 150, etc.
                            colors = SliderDefaults.colors(
                                thumbColor = PassThePigsColors.Primary,
                                activeTrackColor = PassThePigsColors.Primary
                            )
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "50",
                                fontSize = 12.sp,
                                color = PassThePigsColors.OnBackground.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "500",
                                fontSize = 12.sp,
                                color = PassThePigsColors.OnBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
        
        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PassThePigsColors.Secondary.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Settings Info",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PassThePigsColors.Secondary
                )
                
                Text(
                    text = "• Changes apply to new games only\n• Current game settings remain unchanged\n• Settings are saved automatically",
                    fontSize = 14.sp,
                    color = PassThePigsColors.OnBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}