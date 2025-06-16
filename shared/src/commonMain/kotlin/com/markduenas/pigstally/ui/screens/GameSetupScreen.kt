package com.markduenas.pigstally.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.markduenas.pigstally.ui.components.PlayerManagementCard
import com.markduenas.pigstally.ui.theme.PassThePigsColors

@Composable
fun GameSetupScreen(
    gameState: GameState,
    viewModel: GameViewModel,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        SetupHeader()
        
        // Players list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(gameState.players) { index, player ->
                PlayerManagementCard(
                    player = player,
                    playerIndex = index,
                    onUpdateName = { newName ->
                        viewModel.updatePlayerName(player.id, newName)
                    },
                    onRemove = {
                        viewModel.removePlayer(player.id)
                    },
                    canRemove = gameState.players.size > 2
                )
            }
        }
        
        // Add player button
        if (gameState.players.size < 6) {
            Button(
                onClick = { viewModel.addPlayer() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PassThePigsColors.Secondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add player"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Player (${gameState.players.size}/6)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Game info
        GameInfoCard(playersCount = gameState.players.size)
        
        // Start game button
        Button(
            onClick = {
                viewModel.startGame()
                onStartGame()
            },
            enabled = gameState.players.size >= 2,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PassThePigsColors.Primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Start Game",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (gameState.players.size < 2) {
            Text(
                text = "Need at least 2 players to start",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = PassThePigsColors.NegativeScore,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SetupHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Game Setup",
            fontSize = 18.sp,
            color = PassThePigsColors.OnBackground
        )
    }
}

@Composable
private fun GameInfoCard(playersCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PassThePigsColors.Primary.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Game Rules",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PassThePigsColors.Primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                text = "• Roll pigs to score points\n• First to 100 points wins\n• Bank points or risk losing your turn\n• Avoid penalties that can eliminate you!",
                fontSize = 14.sp,
                color = PassThePigsColors.OnBackground,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Players: $playersCount/6",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PassThePigsColors.Primary
            )
        }
    }
}