package com.uzazi.app.feature.nightcompanion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzazi.app.core.data.UzaziDataStore
import com.uzazi.app.core.utils.DistressDetector
import com.uzazi.app.domain.models.ChatMessage
import com.uzazi.app.domain.models.MessageRole
import com.uzazi.app.domain.usecases.StreamChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NightCompanionViewModel @Inject constructor(
    private val streamChatMessageUseCase: StreamChatMessageUseCase,
    private val dataStore: UzaziDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(NightCompanionUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NightCompanionEvent>()
    val events = _events.asSharedFlow()

    private val systemPrompt = """
        You are Mama Bear, a warm caring companion for a new mother who may be struggling. It is late at night. Rules: speak in short sentences max 2-3 per response. Always validate feelings first. Never diagnose or use clinical terms. Never say postpartum depression. If severe distress: respond only with the safety message. Offer breathing exercise after 2 exchanges if distressed. Speak in user's language if Swahili, Amharic or Hausa. End every 4th message with: You are doing better than you think.
    """.trimIndent()

    init {
        initializeSession()
    }

    private fun initializeSession() {
        viewModelScope.launch {
            dataStore.recordNightSession()
        }
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 22..23 -> "It's late, mama. I'm here. What's on your mind?"
            in 0..2 -> "You're awake. That's okay. I'm here with you."
            in 3..4 -> "The hardest hours. I'm not going anywhere. Tell me how you're feeling."
            else -> "Almost morning, mama. You made it through the night."
        }

        val initialMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = greeting,
            role = MessageRole.ASSISTANT,
            timestamp = Date()
        )

        _uiState.update { it.copy(messages = listOf(initialMessage)) }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            role = MessageRole.USER,
            timestamp = Date()
        )

        _uiState.update { 
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isTyping = true
            ) 
        }

        val distressLevel = DistressDetector.detect(text)
        _uiState.update { it.copy(distressLevel = distressLevel) }

        if (distressLevel == DistressDetector.DistressLevel.SEVERE) {
            val safetyMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "You are so important, mama. Please reach out to your health worker right away. Tap the button below — you don't have to feel this alone.",
                role = MessageRole.ASSISTANT,
                timestamp = Date()
            )
            _uiState.update { 
                it.copy(
                    messages = it.messages + safetyMessage,
                    showChwButton = true,
                    isTyping = false
                ) 
            }
            viewModelScope.launch {
                dataStore.recordSoughtHelp()
            }
            return
        }

        if (distressLevel == DistressDetector.DistressLevel.MILD) {
            _uiState.update { it.copy(showBreathingExercise = true) }
            _pendingMessage = text
        } else {
            triggerApiCall(text)
        }
    }

    private var _pendingMessage: String? = null

    fun dismissBreathingExercise() {
        _uiState.update { it.copy(showBreathingExercise = false) }
        _pendingMessage?.let {
            triggerApiCall(it)
            _pendingMessage = null
        }
    }

    private fun triggerApiCall(text: String) {
        viewModelScope.launch {
            streamChatMessageUseCase(
                userMessage = text,
                sessionId = _uiState.value.sessionId,
                onComplete = { finalResponse ->
                    var response = finalResponse
                    val nextExchangeCount = _uiState.value.exchangeCount + 1
                    
                    if (nextExchangeCount % 4 == 0) {
                        response += " You are doing better than you think."
                    }

                    val assistantMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = response,
                        role = MessageRole.ASSISTANT,
                        timestamp = Date()
                    )

                    _uiState.update {
                        it.copy(
                            messages = it.messages + assistantMessage,
                            currentStreamingContent = "",
                            isTyping = false,
                            exchangeCount = nextExchangeCount,
                            showChwButton = it.showChwButton || (nextExchangeCount >= 3 && it.distressLevel != DistressDetector.DistressLevel.NONE)
                        )
                    }
                }
            ).collect { token ->
                _uiState.update { it.copy(currentStreamingContent = it.currentStreamingContent + token) }
            }
        }
    }

    fun openWhatsAppForChw() {
        viewModelScope.launch {
            dataStore.recordSoughtHelp()
            _events.emit(NightCompanionEvent.OpenWhatsApp)
        }
    }
}

data class NightCompanionUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val showBreathingExercise: Boolean = false,
    val distressLevel: DistressDetector.DistressLevel = DistressDetector.DistressLevel.NONE,
    val showChwButton: Boolean = false,
    val currentStreamingContent: String = "",
    val sessionId: String = UUID.randomUUID().toString(),
    val inputText: String = "",
    val exchangeCount: Int = 0
)

sealed class NightCompanionEvent {
    object OpenWhatsApp : NightCompanionEvent()
}
