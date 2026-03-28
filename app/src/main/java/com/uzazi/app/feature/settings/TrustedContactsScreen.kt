package com.uzazi.app.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uzazi.app.ui.theme.BloomPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedContactsScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrustedContactsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TrustedContactsEvent.SaveSuccess -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trusted People") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .padding(16.dp)
        ) {
            Text(
                "Who can we call if we're worried about you?",
                style = MaterialTheme.typography.titleMedium,
                color = BloomPink
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "If you go quiet for 3 days, we'll send them a gentle message. They won't see your health data.",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = uiState.phoneInput,
                onValueChange = viewModel::updatePhone,
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = viewModel::saveContact,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BloomPink),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = androidx.compose.ui.graphics.Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Contact")
                }
            }
        }
    }
}
