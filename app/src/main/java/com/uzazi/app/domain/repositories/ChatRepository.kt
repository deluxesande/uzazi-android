package com.uzazi.app.domain.repositories

import com.uzazi.app.domain.models.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun streamMessage(userMessage: String, sessionId: String, customInstruction: String? = null): Flow<String>
    suspend fun saveMessage(message: ChatMessage)
    fun getSessionMessages(sessionId: String): Flow<List<ChatMessage>>
    suspend fun clearOldSessions()
}
