package com.uzazi.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uzazi.app.core.utils.NightModeDetector
import com.uzazi.app.domain.models.RiskLevel
import com.uzazi.app.ui.components.RiskBadge
import com.uzazi.app.ui.theme.BloomPink

@Composable
fun HomeScreen(
    onNavigateToCheckIn: () -> Unit,
    onNavigateToResult: () -> Unit,
    onNavigateToCompanion: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTrustedContacts: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isNightTime = NightModeDetector.isNightTime()
    val scrollState = rememberScrollState()
    var showMenu by remember { mutableStateOf(false) }

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
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StreakBadge(streak = uiState.streakCount)
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToSettings()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Trusted People") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToTrustedContacts()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("History") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToHistory()
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCompanion,
                icon = { Text("🐻", fontSize = 20.sp) },
                text = { Text("Chat with Mama Bear") },
                containerColor = BloomPink,
                contentColor = Color.White
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isNightTime) "Good evening, Deluxe" else "Good morning, Deluxe",
//                text = if (isNightTime) "Good evening, ${uiState.userName}" else "Good morning, ${uiState.userName}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Day ${uiState.postpartumDay} of your journey",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
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
                            Icon(Icons.Default.Close, contentDescription = "Dismiss")
                        }
                    }
                }
            }

            Text(
                text = "Your bloom garden · ${uiState.petalCount} petals earned",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF993556),
                modifier = Modifier.align(Alignment.Start).padding(horizontal = 20.dp, vertical = 0.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            GardenCanvas(
                petalCount = uiState.petalCount,
                totalSlots = 30,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(value = uiState.postpartumDay.toString(), label = "days active", modifier = Modifier.weight(1f))
                StatCard(value = uiState.petalCount.toString(), label = "petals earned", modifier = Modifier.weight(1f))
                StatCard(value = uiState.badgeCount.toString(), label = "badges", modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (uiState.todayCheckedIn) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToResult() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("You bloomed today ✓", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFD4537E))
                            Text("Results are ready to view", fontSize = 11.sp, color = Color(0xFF72243E))
                        }
                        if (uiState.lastRiskLevel != RiskLevel.UNKNOWN) {
                            RiskBadge(level = uiState.lastRiskLevel)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToCheckIn() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD4537E))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp, 16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Today's check-in", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("5 questions · earn 3 petals", color = Color(0xFFF4C0D1), fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFFD4537E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigateToCompanion() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isNightTime) Color(0xFF1A237E) else Color(0xFFFCE4EC))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp, 16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🐻", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (isNightTime) "Mama Bear is awake" else "Talk to Mama Bear",
                            color = if (isNightTime) Color.White else Color(0xFF880E4F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Always here to listen and support you",
                            color = if (isNightTime) Color.LightGray else Color(0xFFAD1457),
                            fontSize = 11.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFFFF0F5), RoundedCornerShape(14.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4537E)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF72243E)
            )
        }
    }
}
