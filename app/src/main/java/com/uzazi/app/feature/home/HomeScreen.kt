package com.uzazi.app.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uzazi.app.core.utils.NightModeDetector
import com.uzazi.app.ui.theme.BloomPink

@Composable
fun HomeScreen(
    onNavigateToCheckIn: () -> Unit,
    onNavigateToResult: () -> Unit,
    onNavigateToCompanion: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isNightTime = NightModeDetector.isNightTime()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Uzazi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = BloomPink)
                StreakBadge(streak = uiState.streakCount)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isNightTime) "Good evening, ${uiState.userName}" else "Good morning, ${uiState.userName}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Day ${uiState.postpartumDay} of your journey",
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (uiState.showComebackMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomPink.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌸", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Welcome back, mama. Your garden missed you. Here are 2 bonus petals 🌸",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = viewModel::dismissComebackMessage) {
                            Icon(androidx.compose.material.icons.Icons.Default.Close, contentDescription = "Dismiss")
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard("Days", uiState.postpartumDay.toString())
                StatCard("Petals", uiState.petalCount.toString())
                StatCard("Badges", uiState.badgesEarned.toString())
            }
            
            Spacer(modifier = Modifier.weight(1f))
            GardenCanvas(petalCount = uiState.petalCount)
            Spacer(modifier = Modifier.weight(1f))
            
            DailyCtaCard(
                todayCheckedIn = uiState.todayCheckedIn,
                lastRiskLevel = uiState.lastRiskLevel,
                isNightTime = isNightTime,
                onCheckInClick = onNavigateToCheckIn,
                onResultClick = onNavigateToResult,
                onCompanionClick = onNavigateToCompanion
            )
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.width(90.dp)) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
