package com.uzazi.app.feature.checkin

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.theme.BloomPink

@Composable
fun CheckInProgressBar(current: Int, total: Int) {
    val progress by animateFloatAsState(targetValue = current.toFloat() / total, label = "progress")
    
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Question $current of $total", style = MaterialTheme.typography.labelMedium)
            Text(text = "🌸", color = BloomPink)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = BloomPink
        )
    }
}
