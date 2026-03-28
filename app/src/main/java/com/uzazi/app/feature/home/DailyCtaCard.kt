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
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        
        if (isNightTime) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NightBlue)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("It's late, mama. I'm here.", fontWeight = FontWeight.Bold, color = Color.White)
                    Text("I'm awake if you need to talk.", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onCompanionClick, 
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    ) {
                        Text("Open night companion", color = Color.White)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SoftRose)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (todayCheckedIn) "You've bloomed today ✓" else "Your garden is waiting 🌸", 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (todayCheckedIn) "You can check in again if anything changes." else "Check in to earn your daily petal.", 
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (todayCheckedIn && lastRiskLevel != RiskLevel.UNKNOWN) {
                    Spacer(modifier = Modifier.height(8.dp))
                    RiskBadge(level = lastRiskLevel)
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onCheckInClick, 
                    colors = ButtonDefaults.buttonColors(containerColor = BloomPink),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) {
                    Text(if (todayCheckedIn) "Check in again" else "Start check-in")
                }

                if (todayCheckedIn) {
                    TextButton(
                        onClick = onResultClick,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("See today's results", color = BloomPink)
                    }
                }
            }
        }
    }
}
