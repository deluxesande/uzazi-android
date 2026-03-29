package com.uzazi.app.feature.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uzazi.app.domain.models.RiskLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val history by viewModel.historyState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Analysis History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No past analyses found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { (id, result) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when(result.riskLevel) {
                                RiskLevel.LOW -> Color(0xFFE8F5E9)
                                RiskLevel.MEDIUM -> Color(0xFFFFF3E0)
                                RiskLevel.HIGH -> Color(0xFFFFEBEE)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = "Risk: ${result.riskLevel}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = when(result.riskLevel) {
                                        RiskLevel.LOW -> Color(0xFF2E7D32)
                                        RiskLevel.MEDIUM -> Color(0xFFEF6C00)
                                        RiskLevel.HIGH -> Color(0xFFC62828)
                                        else -> Color.Gray
                                    }
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = result.clinicalSummary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
