package com.uzazi.app.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
    var otp by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf(60) }
    val focusRequesters = remember { List(6) { FocusRequester() } }

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
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(6) { index ->
                OutlinedTextField(
                    value = if (otp.length > index) otp[index].toString() else "",
                    onValueChange = { value ->
                        if (value.length <= 1) {
                            val newOtp = otp.toCharArray().toMutableList()
                            if (index < otp.length) {
                                newOtp[index] = value.getOrNull(0) ?: ' '
                            } else if (value.isNotEmpty()) {
                                newOtp.add(value[0])
                            }
                            otp = newOtp.joinToString("").trim()
                            
                            if (value.isNotEmpty() && index < 5) {
                                focusRequesters[index + 1].requestFocus()
                            }
                            
                            if (otp.length == 6) {
                                onVerifyOtp(otp)
                            }
                        }
                    },
                    modifier = Modifier
                        .width(45.dp)
                        .focusRequester(focusRequesters[index]),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (timer > 0) "Resend in 00:${timer.toString().padStart(2, '0')}" else "Resend code",
            color = if (timer > 0) Color.Gray else MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        
        TextButton(onClick = onBack) {
            Text("Change phone number")
        }
    }
}
