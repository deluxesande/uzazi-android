package com.uzazi.app.feature.share

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uzazi.app.ui.theme.BloomPink
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    onNavigateToQr: (String, Long) -> Unit,
    viewModel: ShareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Share update") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BloomPink.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your summary", fontWeight = FontWeight.Bold, color = BloomPink)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(uiState.summaryText, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Send to ${uiState.chwName}", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(12.dp))

            ShareOptionCard(
                icon = Icons.Default.Email, // Placeholder for WhatsApp
                label = "WhatsApp",
                subtitle = "Fast and secure",
                onClick = {
                    val encoded = URLEncoder.encode(uiState.summaryText, "UTF-8")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${uiState.chwPhone}?text=$encoded"))
                    context.startActivity(intent)
                }
            )

            ShareOptionCard(
                icon = Icons.Default.AccountBox,
                label = "QR Code",
                subtitle = "Show to worker in person",
                onClick = {
                    if (uiState.qrToken == null) {
                        viewModel.generateQrToken()
                    } else {
                        onNavigateToQr(uiState.qrToken!!, uiState.qrExpiry)
                    }
                }
            )

            ShareOptionCard(
                icon = Icons.Default.Send,
                label = "SMS",
                subtitle = "Standard text message",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${uiState.chwPhone}"))
                    intent.putExtra("sms_body", uiState.summaryText)
                    context.startActivity(intent)
                }
            )

            ShareOptionCard(
                icon = Icons.Default.DateRange,
                label = "Download PDF",
                subtitle = "Coming soon",
                onClick = { /* Snackbar logic if needed */ }
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Only what you approve leaves this app",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }

    if (uiState.qrToken != null) {
        onNavigateToQr(uiState.qrToken!!, uiState.qrExpiry)
    }
}

@Composable
fun ShareOptionCard(icon: ImageVector, label: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .minHeight(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = BloomPink)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Open")
        }
    }
}
