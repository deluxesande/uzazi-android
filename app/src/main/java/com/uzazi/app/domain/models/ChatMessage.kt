package com.uzazi.app.domain.models

import java.util.Date

data class ChatMessage(
    val id: String,
    val text: String,
    val role: MessageRole,
    val timestamp: Date
)

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}
