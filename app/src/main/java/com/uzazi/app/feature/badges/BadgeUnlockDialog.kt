package com.uzazi.app.feature.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.uzazi.app.domain.models.Badge
import com.uzazi.app.ui.theme.BloomPink

@Composable
fun BadgeUnlockDialog(
    badge: Badge,
    onDismiss: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://assets9.lottiefiles.com/packages/lf20_tou9dfsq.json"))
    val progress by animateLottieCompositionAsState(composition)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )

            if (progress > 0.3f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = mapBadgeToEmoji(badge.id),
                        fontSize = 80.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = badge.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = BloomPink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = BloomPink)
                    ) {
                        Text("Woohoo!")
                    }
                }
            }
        }
    }
}

fun mapBadgeToEmoji(id: String): String = when (id) {
    "1" -> "🌱"
    "2" -> "🌿"
    "3" -> "🌸"
    "4" -> "🌺"
    "5" -> "🌻"
    "6" -> "🦉"
    "7" -> "🗣️"
    "8" -> "🤝"
    else -> "🏅"
}
