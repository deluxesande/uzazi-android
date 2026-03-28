package com.uzazi.app.feature.checkin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.uzazi.app.domain.models.RiskLevel
import com.uzazi.app.ui.components.MamaBearAvatar
import com.uzazi.app.ui.theme.*

@Composable
fun ResultScreen(
    riskLevelStr: String,
    onBackToGarden: () -> Unit
) {
    val riskLevel = try { RiskLevel.valueOf(riskLevelStr) } catch (e: Exception) { RiskLevel.UNKNOWN }
    
    val (title, description, color, actionText) = when (riskLevel) {
        RiskLevel.LOW -> Quad(
            "You bloomed today!",
            "Your garden is looking healthy. Keep taking care of yourself, mama.",
            MintGreen,
            "Continue blooming"
        )
        RiskLevel.MEDIUM -> Quad(
            "We're here for you",
            "It sounds like you're having a tough day. Try some deep breathing or talk to a friend.",
            WarmAmber,
            "See tips"
        )
        RiskLevel.HIGH -> Quad(
            "Let's get you some support",
            "I'm a bit worried about you. Would you like to message your health worker?",
            AlertRed,
            "Contact my health worker"
        )
        else -> Quad("Check-in complete", "Thank you for checking in.", BloomPink, "Done")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MamaBearAvatar(size = 150.dp)
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (riskLevel == RiskLevel.HIGH) {
            Button(
                onClick = { /* Open WhatsApp */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
            ) {
                Text("Contact my health worker")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Button(
            onClick = onBackToGarden,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BloomPink)
        ) {
            Text("Back to garden")
        }
    }
}

data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
