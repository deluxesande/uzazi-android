package com.uzazi.app.data.repositories

import com.uzazi.app.core.data.daos.ChatMessageDao
import com.uzazi.app.core.data.entities.ChatMessageEntity
import com.uzazi.app.core.network.SseClient
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.domain.models.ChatMessage
import com.uzazi.app.domain.models.MessageRole
import com.uzazi.app.domain.repositories.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val sseClient: SseClient,
    private val chatMessageDao: ChatMessageDao,
    private val secureStorage: SecureStorage
) : ChatRepository {

    override fun streamMessage(userMessage: String, sessionId: String): Flow<String> {
        val authToken = secureStorage.getString(SecureStorage.KEY_AUTH_TOKEN) ?: ""
        val language = secureStorage.getString(SecureStorage.KEY_LANGUAGE) ?: "en"
        return sseClient.streamChat(userMessage, sessionId, authToken, language)
    }

    override suspend fun saveMessage(message: ChatMessage) {
        val entity = ChatMessageEntity(
            id = message.id,
            text = message.text,
            role = message.role.name,
            timestamp = message.timestamp.time
        )
        chatMessageDao.insertMessage(entity)
    }

    override fun getSessionMessages(sessionId: String): Flow<List<ChatMessage>> {
        return chatMessageDao.getSessionMessages(sessionId).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    text = entity.text,
                    role = MessageRole.valueOf(entity.role),
                    timestamp = Date(entity.timestamp)
                )
            }
        }
    }

    override suspend fun clearOldSessions() {
        val threshold = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // 24 hours
        chatMessageDao.deleteOldMessages(threshold)
    }
}
