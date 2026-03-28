package com.uzazi.app.feature.share

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(
    qrToken: String,
    expiry: Long,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    var timeRemaining by remember { mutableStateOf("") }
    val qrBitmap = remember(qrToken) { generateQrBitmap(qrToken) }

    LaunchedEffect(expiry) {
        while (true) {
            val diff = expiry - System.currentTimeMillis()
            if (diff <= 0) {
                timeRemaining = "Expired"
                break
            }
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
            timeRemaining = String.format("%02d:%02d remaining", hours, minutes)
            delay(60000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share via QR") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Valid for 24 hours", style = MaterialTheme.typography.labelLarge)
            Text(timeRemaining, color = androidx.compose.ui.graphics.Color.Gray)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        setImageBitmap(qrBitmap)
                    }
                },
                modifier = Modifier.size(250.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (timeRemaining == "Expired") {
                Button(onClick = onRefresh) {
                    Text("Refresh QR")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                var expanded by remember { mutableStateOf(false) }
                Column(modifier = Modifier.padding(16.dp)) {
                    TextButton(onClick = { expanded = !expanded }) {
                        Text("What your health worker sees ${if (expanded) "▲" else "▼"}")
                    }
                    if (expanded) {
                        Text("• Risk assessment score", style = MaterialTheme.typography.bodySmall)
                        Text("• Mood history (14 days)", style = MaterialTheme.typography.bodySmall)
                        Text("• Flagged symptoms", style = MaterialTheme.typography.bodySmall)
                        Text("• Garden progress (Streaks)", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

private fun generateQrBitmap(token: String): Bitmap? {
    return try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(token, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}
