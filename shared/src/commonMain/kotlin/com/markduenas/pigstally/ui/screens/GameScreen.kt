package com.markduenas.pigstally.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markduenas.pigstally.game.GameViewModel
import com.markduenas.pigstally.model.GameState
import com.markduenas.pigstally.model.PigPair
import com.markduenas.pigstally.model.ScoringPosition
import com.markduenas.pigstally.ui.components.PlayerCard
import com.markduenas.pigstally.ui.components.ScoringButton
import com.markduenas.pigstally.ui.theme.PassThePigsColors

@Composable
fun GameScreen(
    gameState: GameState,
    viewModel: GameViewModel,
    onNavigateToSetup: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Game header
        GameHeader(
            gameState = gameState,
            onNewGame = { viewModel.newGame(); onNavigateToSetup() },
            onNavigateToSettings = onNavigateToSettings
        )
        
        // Players display
        PlayersSection(
            gameState = gameState,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Scoring section
        if (gameState.gameStarted && !gameState.gameEnded) {
            ScoringSection(
                gameState = gameState,
                onScore = { position -> viewModel.scorePig(position) },
                onBankPoints = { viewModel.bankPoints() },
                onRollAgain = { viewModel.rollAgain() },
                onUndo = { viewModel.undoLastRoll() },
                modifier = Modifier.weight(1f)
            )
        } else if (gameState.gameEnded) {
            GameEndedSection(
                winner = gameState.winner,
                onNewGame = { viewModel.newGame(); onNavigateToSetup() },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GameHeader(
    gameState: GameState,
    onNewGame: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Pigs Tally",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PassThePigsColors.Primary
            )
            if (gameState.gameStarted) {
                Text(
                    text = "Round ${gameState.roundNumber} • Target: ${gameState.winningScore}",
                    fontSize = 14.sp,
                    color = PassThePigsColors.OnBackground
                )
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateToSettings
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = PassThePigsColors.Primary
                )
            }
            
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PassThePigsColors.Primary
                )
            ) {
                Text("New Game")
            }
        }
    }
}

@Composable
private fun PlayersSection(
    gameState: GameState,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        itemsIndexed(gameState.players) { index, player ->
            PlayerCard(
                player = player,
                playerIndex = index,
                isCurrentPlayer = index == gameState.currentPlayerIndex,
                currentTurnScore = if (index == gameState.currentPlayerIndex) gameState.currentTurnScore else 0,
                modifier = Modifier
                    .fillParentMaxWidth(1f / gameState.players.size.coerceAtMost(4))
                    .padding(horizontal = 2.dp)
            )
        }
    }
}

@Composable
private fun ScoringSection(
    gameState: GameState,
    onScore: (ScoringPosition) -> Unit,
    onBankPoints: () -> Unit,
    onRollAgain: () -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Two-column layout for turn state and pig rolls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First column: Turn state
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = PassThePigsColors.Primary.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "${gameState.currentPlayer?.name}'s Turn",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PassThePigsColors.Primary,
                            textAlign = TextAlign.Center
                        )

                        // Pig roll status
                        val turnState = gameState.currentTurnState
                        val pigStatus = when {
                            turnState.canRollAgain -> "Bank or roll again?"
                            turnState.hasPenalty -> "Penalty! Turn ends"
                            turnState.currentPigNumber == 1 -> "Enter Pig 1 of 2"
                            turnState.currentPigNumber == 2 -> "Enter Pig 2 of 2"
                            else -> ""
                        }

                        Text(
                            text = "Turn Score: ${gameState.currentTurnScore}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = PassThePigsColors.PositiveScore,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = pigStatus,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PassThePigsColors.OnBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Second column: Current pig rolls
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = PassThePigsColors.Primary.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val turnState = gameState.currentTurnState
                    if (turnState.allRolls.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            // Show current roll with score
                            if (turnState.currentPig1 != null || turnState.currentPig2 != null) {
                                val currentScore = (turnState.currentPig1?.points ?: 0) + (turnState.currentPig2?.points ?: 0)
                                val currentPairBonus = if (turnState.hasCurrentPair) {
                                    val currentPair = PigPair(turnState.currentPig1!!, turnState.currentPig2!!)
                                    currentPair.getDoubleBonus()
                                } else 0
                                val totalCurrentScore = currentScore + currentPairBonus
                                
                                Text(
                                    text = "Current roll: ${totalCurrentScore} pts",
                                    fontSize = 14.sp,
                                    color = PassThePigsColors.Primary,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                // Show individual pig details
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    turnState.currentPig1?.let { pig1 ->
                                        Text(
                                            text = "Pig 1: ${pig1.position.displayName} (${pig1.points})",
                                            fontSize = 11.sp,
                                            color = PassThePigsColors.OnBackground,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    turnState.currentPig2?.let { pig2 ->
                                        Text(
                                            text = "Pig 2: ${pig2.position.displayName} (${pig2.points})",
                                            fontSize = 11.sp,
                                            color = PassThePigsColors.OnBackground,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                
                                if (currentPairBonus > 0) {
                                    Text(
                                        text = "(includes +${currentPairBonus} double bonus)",
                                        fontSize = 10.sp,
                                        color = PassThePigsColors.PositiveScore,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                            else {
                                // Show completed pairs count if any
                                if (turnState.completedPairs.isNotEmpty()) {
                                    Text(
                                        text = "Completed: ${turnState.completedPairs.size} ${if (turnState.completedPairs.size == 1) "pair!" else "pairs!"}",
                                        fontSize = 12.sp,
                                        color = PassThePigsColors.PositiveScore,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No rolls yet",
                            fontSize = 12.sp,
                            color = PassThePigsColors.OnBackground.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
        
        // Scoring buttons grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(getScoringPositions()) { position ->
                ScoringButton(
                    position = position,
                    onScore = onScore,
                    enabled = !gameState.gameEnded && !gameState.currentTurnState.canRollAgain,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Action buttons
        val turnState = gameState.currentTurnState
        if (turnState.canRollAgain) {
            // Show bank and roll again options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onUndo,
                    enabled = turnState.allRolls.isNotEmpty(),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Undo")
                }
                
                Button(
                    onClick = onBankPoints,
                    enabled = gameState.currentTurnScore > 0,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PassThePigsColors.PositiveScore
                    )
                ) {
                    Text("Bank")
                }
                
                Button(
                    onClick = onRollAgain,
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PassThePigsColors.Primary
                    )
                ) {
                    Text("Roll Again")
                }
            }
        } else {
            // Show normal action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onUndo,
                    enabled = turnState.allRolls.isNotEmpty(),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Undo")
                }
            }
        }
    }
}

@Composable
private fun GameEndedSection(
    winner: com.markduenas.pigstally.model.Player?,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Game Over!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PassThePigsColors.Primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (winner != null) {
            Text(
                text = "${winner.name} Wins!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PassThePigsColors.PositiveScore
            )
            
            Text(
                text = "Final Score: ${winner.totalScore}",
                fontSize = 18.sp,
                color = PassThePigsColors.OnBackground
            )
        } else {
            Text(
                text = "No Winner",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PassThePigsColors.NegativeScore
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onNewGame,
            colors = ButtonDefaults.buttonColors(
                containerColor = PassThePigsColors.Primary
            )
        ) {
            Text("Start New Game")
        }
    }
}

private fun getScoringPositions(): List<ScoringPosition> {
    return listOf(
        ScoringPosition.DOT_UP,
        ScoringPosition.DOT_DOWN,
        ScoringPosition.TROTTER,
        ScoringPosition.RAZORBACK,
        ScoringPosition.SNOUTER,
        ScoringPosition.LEANING_JOWLER,
        ScoringPosition.OINKER,
        ScoringPosition.PIGGYBACK
    )
}