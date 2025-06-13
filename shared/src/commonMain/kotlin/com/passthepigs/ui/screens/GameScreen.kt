package com.passthepigs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.passthepigs.game.GameViewModel
import com.passthepigs.model.GameState
import com.passthepigs.model.ScoringPosition
import com.passthepigs.ui.components.PlayerCard
import com.passthepigs.ui.components.ScoringButton
import com.passthepigs.ui.theme.PassThePigsColors

@Composable
fun GameScreen(
    gameState: GameState,
    viewModel: GameViewModel,
    onNavigateToSetup: () -> Unit,
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
            onNewGame = { viewModel.newGame(); onNavigateToSetup() }
        )
        
        // Players display
        PlayersSection(
            gameState = gameState,
            modifier = Modifier.height(128.dp)
        )
        
        // Scoring section
        if (gameState.gameStarted && !gameState.gameEnded) {
            ScoringSection(
                gameState = gameState,
                onScore = { position -> viewModel.scorePig(position) },
                onBankPoints = { viewModel.bankPoints() },
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
    onNewGame: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Pass the Pigs",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PassThePigsColors.Primary
            )
            if (gameState.gameStarted) {
                Text(
                    text = "Round ${gameState.roundNumber}",
                    fontSize = 14.sp,
                    color = PassThePigsColors.OnBackground
                )
            }
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

@Composable
private fun PlayersSection(
    gameState: GameState,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        itemsIndexed(gameState.players) { index, player ->
            PlayerCard(
                player = player,
                playerIndex = index,
                isCurrentPlayer = index == gameState.currentPlayerIndex,
                currentTurnScore = if (index == gameState.currentPlayerIndex) gameState.currentTurnScore else 0,
                modifier = Modifier.width(160.dp)
            )
        }
    }
}

@Composable
private fun ScoringSection(
    gameState: GameState,
    onScore: (ScoringPosition) -> Unit,
    onBankPoints: () -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current player and turn score
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PassThePigsColors.Primary.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${gameState.currentPlayer?.name}'s Turn",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PassThePigsColors.Primary
                )
                
                // Pig roll status
                val turnState = gameState.currentTurnState
                val pigStatus = when {
                    turnState.isComplete -> "Turn Complete"
                    turnState.currentPigNumber == 1 -> "Enter Pig 1 of 2"
                    turnState.currentPigNumber == 2 -> "Enter Pig 2 of 2"
                    else -> ""
                }
                
                Text(
                    text = pigStatus,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PassThePigsColors.OnBackground
                )
                
                // Show current pig rolls
                if (turnState.pig1 != null || turnState.pig2 != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        turnState.pig1?.let { pig1 ->
                            Text(
                                text = "Pig 1: ${pig1.position.displayName} (${pig1.points})",
                                fontSize = 12.sp,
                                color = PassThePigsColors.OnBackground
                            )
                        }
                        turnState.pig2?.let { pig2 ->
                            Text(
                                text = "Pig 2: ${pig2.position.displayName} (${pig2.points})",
                                fontSize = 12.sp,
                                color = PassThePigsColors.OnBackground
                            )
                        }
                    }
                }
                
                if (gameState.currentTurnScore > 0) {
                    Text(
                        text = "Turn Score: ${gameState.currentTurnScore}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = PassThePigsColors.PositiveScore
                    )
                }
            }
        }
        
        // Scoring buttons grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(getScoringPositions()) { position ->
                ScoringButton(
                    position = position,
                    onScore = onScore,
                    enabled = !gameState.gameEnded
                )
            }
        }
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onUndo,
                enabled = gameState.currentTurnState.pig1 != null || gameState.currentTurnState.pig2 != null,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PassThePigsColors.NeutralScore
                )
            ) {
                Text("Undo")
            }
            
            Button(
                onClick = onBankPoints,
                enabled = gameState.currentTurnState.isComplete && gameState.currentTurnScore > 0,
                modifier = Modifier.weight(2f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PassThePigsColors.PositiveScore
                )
            ) {
                Text("Bank Points")
            }
        }
    }
}

@Composable
private fun GameEndedSection(
    winner: com.passthepigs.model.Player?,
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
        ScoringPosition.SIDER,
        ScoringPosition.TROTTER,
        ScoringPosition.RAZORBACK,
        ScoringPosition.SNOUTER,
        ScoringPosition.LEANING_JOWLER,
        ScoringPosition.PIG_OUT,
        ScoringPosition.OINKER,
        ScoringPosition.PIGGYBACK
    )
}