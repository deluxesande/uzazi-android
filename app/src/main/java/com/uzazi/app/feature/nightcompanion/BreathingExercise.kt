package com.uzazi.app.feature.nightcompanion

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uzazi.app.ui.components.MamaBearAvatar
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.NightBlue

@Composable
fun BreathingExercise(onDismiss: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 11f, // 4s inhale, 1s hold, 6s exhale
        animationSpec = infiniteRepeatable(
            animation = tween(11000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "phase"
    )

    val radius = when {
        phase <= 4f -> 60f + (60f * (phase / 4f)) // Inhale 60 to 120
        phase <= 5f -> 120f // Hold at 120
        else -> 120f - (60f * ((phase - 5f) / 6f)) // Exhale 120 to 60
    }

    val textInstruction = when {
        phase <= 4f -> "breathe in..."
        phase <= 5f -> "hold..."
        else -> "breathe out..."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBlue.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MamaBearAvatar(size = 80.dp, nightMode = true)
            Spacer(modifier = Modifier.height(48.dp))

            Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = BloomPink.copy(alpha = 0.2f),
                        radius = radius.dp.toPx() + 10.dp.toPx()
                    )
                    drawCircle(
                        color = BloomPink.copy(alpha = 0.6f),
                        radius = radius.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = textInstruction,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BloomPink)
            ) {
                Text("I feel a little calmer")
            }
        }
    }
}
