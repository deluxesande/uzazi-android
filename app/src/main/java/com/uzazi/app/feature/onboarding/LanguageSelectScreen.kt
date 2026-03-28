package com.uzazi.app.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uzazi.app.ui.theme.BloomPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectScreen(onLanguageSelected: (String) -> Unit) {
    val languages = listOf(
        Language("en", "English", "English"),
        Language("sw", "Swahili", "Kiswahili"),
        Language("am", "Amharic", "አማርኛ"),
        Language("ha", "Hausa", "Hausa")
    )
    var selectedLang by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "What language do you prefer?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(languages) { lang ->
                Card(
                    onClick = { selectedLang = lang.code },
                    modifier = Modifier.height(120.dp),
                    border = if (selectedLang == lang.code) BorderStroke(2.dp, BloomPink) else null,
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedLang == lang.code) BloomPink.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = lang.nativeName, fontWeight = FontWeight.Bold)
                            Text(text = lang.name, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        
        Button(
            onClick = { selectedLang?.let { onLanguageSelected(it) } },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedLang != null,
            colors = ButtonDefaults.buttonColors(containerColor = BloomPink)
        ) {
            Text("Continue")
        }
    }
}

data class Language(val code: String, val name: String, val nativeName: String)
