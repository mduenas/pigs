@file:OptIn(ExperimentalMaterial3Api::class)

package com.markduenas.pigstally.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markduenas.pigstally.model.Player
import com.markduenas.pigstally.ui.theme.PassThePigsColors

@Composable
fun PlayerManagementCard(
    player: Player,
    playerIndex: Int,
    onUpdateName: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editingName by remember { mutableStateOf(player.name) }
    // val keyboardController = LocalSoftwareKeyboardController.current
    
    val playerColor = PassThePigsColors.getPlayerColor(playerIndex)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player info section
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player number indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = playerColor
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${playerIndex + 1}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                
                // Player name section
                if (isEditing) {
                    OutlinedTextField(
                        value = editingName,
                        onValueChange = { editingName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onUpdateName(editingName.trim())
                                isEditing = false
                                // keyboardController?.hide()
                            }
                        ),
                        placeholder = { Text("Enter player name") }
                    )
                } else {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = player.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = PassThePigsColors.OnSurface
                        )
                        Text(
                            text = "Score: ${player.totalScore}",
                            fontSize = 14.sp,
                            color = playerColor
                        )
                    }
                }
            }
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit button
                IconButton(
                    onClick = {
                        if (isEditing) {
                            onUpdateName(editingName.trim())
                            isEditing = false
                            // keyboardController?.hide()
                        } else {
                            editingName = player.name
                            isEditing = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = if (isEditing) "Save name" else "Edit name",
                        tint = if (isEditing) PassThePigsColors.PositiveScore else PassThePigsColors.OnSurface
                    )
                }
                
                // Remove button
                if (canRemove) {
                    IconButton(
                        onClick = onRemove,
                        enabled = !isEditing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove player",
                            tint = PassThePigsColors.NegativeScore
                        )
                    }
                }
            }
        }
    }
}