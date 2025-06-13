@file:OptIn(ExperimentalMaterial3Api::class)

package com.passthepigs.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.passthepigs.ui.theme.PassThePigsColors

@Composable
fun MixedScoreDialog(
    onScore: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var pointsText by remember { mutableStateOf("") }
    // val keyboardController = LocalSoftwareKeyboardController.current
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = PassThePigsColors.Surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Mixed Score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PassThePigsColors.Primary
                )
                
                Text(
                    text = "Enter the combined points from both pigs landing in different positions:",
                    fontSize = 14.sp,
                    color = PassThePigsColors.OnSurface
                )
                
                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { newValue ->
                        // Only allow digits
                        if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                            pointsText = newValue
                        }
                    },
                    label = { Text("Points") },
                    placeholder = { Text("e.g., 15") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val points = pointsText.toIntOrNull()
                            if (points != null && points > 0) {
                                onScore(points)
                                // keyboardController?.hide()
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "Common combinations:\n" +
                            "Trotter + Snouter = 15 pts\n" +
                            "Razorback + Leaning Jowler = 20 pts\n" +
                            "Snouter + Leaning Jowler = 25 pts",
                    fontSize = 12.sp,
                    color = PassThePigsColors.OnSurface.copy(alpha = 0.7f)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            val points = pointsText.toIntOrNull()
                            if (points != null && points > 0) {
                                onScore(points)
                            }
                        },
                        enabled = pointsText.toIntOrNull()?.let { it > 0 } == true,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PassThePigsColors.PositiveScore
                        )
                    ) {
                        Text("Score")
                    }
                }
            }
        }
    }
}