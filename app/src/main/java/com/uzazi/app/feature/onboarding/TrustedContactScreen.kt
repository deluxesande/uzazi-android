package com.uzazi.app.feature.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.theme.BloomPink

@Composable
fun TrustedContactScreen(onComplete: (String) -> Unit, onSkip: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+254") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Who can we call if we're worried about you?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "If you go quiet for 3 days, we'll send them a gentle message. They won't see your health data.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = countryCode,
                onValueChange = { countryCode = it },
                modifier = Modifier.width(80.dp),
                label = { Text("Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { onComplete("$countryCode$phone") },
            modifier = Modifier.fillMaxWidth(),
            enabled = phone.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = BloomPink)
        ) {
            Text("I'm ready")
        }
        
        TextButton(onClick = onSkip) {
            Text("Skip for now", color = Color.Gray)
        }
    }
}
