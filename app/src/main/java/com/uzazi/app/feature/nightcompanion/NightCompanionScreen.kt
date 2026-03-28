package com.uzazi.app.feature.nightcompanion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uzazi.app.domain.models.ChatMessage
import com.uzazi.app.domain.models.MessageRole
import com.uzazi.app.ui.components.MamaBearAvatar
import com.uzazi.app.ui.theme.BloomPink
import com.uzazi.app.ui.theme.NightBlue
import com.uzazi.app.ui.theme.NightSurface
import com.uzazi.app.ui.theme.SoftRose
import com.uzazi.app.ui.theme.UzaziTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NightCompanionScreen(
    onNavigateBack: () -> Unit,
    viewModel: NightCompanionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size, uiState.isTyping) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size + if (uiState.isTyping || uiState.currentStreamingContent.isNotEmpty()) 1 else 0)
        }
    }

    UzaziTheme(nightMode = true) {
        Box(modifier = Modifier.fillMaxSize().background(NightBlue)) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MamaBearAvatar(size = 40.dp, nightMode = true)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("I'm here, mama", color = SoftRose)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SoftRose)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NightBlue)
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.messages) { message ->
                        ChatBubble(message = message)
                    }
                    
                    if (uiState.currentStreamingContent.isNotEmpty()) {
                        item {
                            ChatBubble(
                                message = ChatMessage(
                                    id = "streaming",
                                    text = uiState.currentStreamingContent,
                                    role = MessageRole.ASSISTANT,
                                    timestamp = Date()
                                ),
                                isStreaming = true
                            )
                        }
                    } else if (uiState.isTyping) {
                        item {
                            TypingIndicator()
                        }
                    }
                }

                if (uiState.showChwButton) {
                    OutlinedButton(
                        onClick = viewModel::openWhatsAppForChw,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftRose)
                    ) {
                        Text("Talk to my health worker")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NightSurface)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.inputText,
                        onValueChange = viewModel::updateInput,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Message...", color = Color.Gray) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = NightBlue,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = BloomPink,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.sendMessage(uiState.inputText) },
                        enabled = uiState.inputText.isNotBlank() && !uiState.isTyping,
                        modifier = Modifier.background(BloomPink, RoundedCornerShape(50))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }

            if (uiState.showBreathingExercise) {
                BreathingExercise(onDismiss = viewModel::dismissBreathingExercise)
            }
        }
    }
}
