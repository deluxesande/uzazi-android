package com.uzazi.app.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uzazi.app.domain.models.RiskLevel
import com.uzazi.app.ui.components.RiskBadge
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.NightBlue
import com.uzazi.app.ui.theme.SoftRose

@Composable
fun DailyCtaCard(
    todayCheckedIn: Boolean,
    lastRiskLevel: RiskLevel,
    isNightTime: Boolean,
    onCheckInClick: () -> Unit,
    onResultClick: () -> Unit,
    onCompanionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isNightTime) NightBlue else SoftRose
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isNightTime -> {
                    Text("It's late, mama. I'm here.", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onCompanionClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                        Text("Open night companion")
                    }
                }
                !todayCheckedIn -> {
                    Text("Your garden is waiting 🌸", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onCheckInClick, colors = ButtonDefaults.buttonColors(containerColor = BloomPink)) {
                        Text("Start check-in")
                    }
                }
                else -> {
                    Text("You bloomed today ✓", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    RiskBadge(level = lastRiskLevel)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onResultClick) {
                        Text("See result")
                    }
                }
            }
        }
    }
}
