package com.uzazi.app.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.components.MamaBearAvatar
import com.uzazi.app.ui.theme.BloomPink

@Composable
fun PhoneInputScreen(
    onSendOtp: (String) -> Unit,
    onGoogleSignIn: (String) -> Unit,
    error: String?
) {
    var phone by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+254") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MamaBearAvatar(size = 120.dp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Welcome, mama",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = BloomPink
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
        
        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onSendOtp("$countryCode$phone") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BloomPink)
        ) {
            Text("Continue with phone")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Divider(modifier = Modifier.weight(1f))
            Text(" or ", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
            Divider(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedButton(
            onClick = { onGoogleSignIn("mock_token") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Google")
        }
    }
}
