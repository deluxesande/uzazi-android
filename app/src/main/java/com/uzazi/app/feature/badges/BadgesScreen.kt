package com.uzazi.app.feature.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uzazi.app.domain.models.Badge
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.SoftRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    viewModel: BadgesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val unlocked = uiState.badges.filter { it.unlockedAt != null }
    val locked = uiState.badges.filter { it.unlockedAt == null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My badges") },
                actions = {
                    Surface(
                        color = BloomPink,
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = unlocked.size.toString(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(padding).padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (unlocked.isNotEmpty()) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
                    Text("Unlocked", fontWeight = FontWeight.Bold, color = BloomPink)
                }
                items(unlocked) { badge ->
                    BadgeCard(badge, isLocked = false)
                }
            }

            if (locked.isNotEmpty()) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
                    Text("Still to earn", fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                items(locked) { badge ->
                    BadgeCard(badge, isLocked = true)
                }
            }
        }

        if (uiState.newlyUnlocked.isNotEmpty()) {
            BadgeUnlockDialog(
                badge = uiState.newlyUnlocked.first(),
                onDismiss = viewModel::dismissUnlockDialog
            )
        }
    }
}

@Composable
fun BadgeCard(badge: Badge, isLocked: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Color.LightGray.copy(alpha = 0.2f) else SoftRose
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = mapBadgeToEmoji(badge.id),
                    fontSize = 32.sp,
                    modifier = Modifier.graphicsLayer {
                        alpha = if (isLocked) 0.4f else 1f
                    }
                )
                if (isLocked) {
                    Text("🔒", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = badge.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 12.sp
            )
            Text(
                text = badge.description,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 11.sp,
                maxLines = 2
            )
        }
    }
}
