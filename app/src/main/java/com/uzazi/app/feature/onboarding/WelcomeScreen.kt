package com.uzazi.app.feature.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.components.MamaBearAvatar
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.SoftRose

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0.5f,
        animationSpec = tween(durationMillis = 1000), label = "avatar_scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftRose)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.scale(scale)) {
            MamaBearAvatar(size = 150.dp)
        }
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Hi mama, I'm here for you.",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = BloomPink
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Uzazi walks with you through the first weeks after birth. No judgment. Just care.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BloomPink)
        ) {
            Text("Let's begin")
        }
    }
}
