package com.uzazi.app.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun OtpVerifyScreen(
    onVerifyOtp: (String) -> Unit,
    onBack: () -> Unit,
    error: String?
) {
    var otpValue by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf(60) }
    
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(Unit) {
        while (timer > 0) {
            delay(1000)
            timer--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Verify your phone", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "We sent a code to your phone",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = otpValue,
            onValueChange = { newValue: String ->
                if (newValue.length <= 6) {
                    otpValue = newValue
                    if (newValue.length == 6) {
                        onVerifyOtp(newValue)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { 
                Text(
                    text = "000000", 
                    modifier = Modifier.fillMaxWidth(), 
                    textAlign = TextAlign.Center
                ) 
            },
            singleLine = true
        )
        
        if (error != null) {
            Text(
                text = error, 
                color = MaterialTheme.colorScheme.error, 
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (timer > 0) "Resend in 00:${timer.toString().padStart(2, '0')}" else "Resend code",
            color = if (timer > 0) Color.Gray else primaryColor,
            modifier = Modifier.padding(8.dp)
        )
        
        TextButton(onClick = onBack) {
            Text("Change phone number")
        }
    }
}
