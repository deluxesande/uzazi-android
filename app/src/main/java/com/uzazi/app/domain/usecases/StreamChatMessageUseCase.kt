package com.uzazi.app.domain.usecases

import com.uzazi.app.domain.models.ChatMessage
import com.uzazi.app.domain.models.MessageRole
import com.uzazi.app.domain.repositories.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject

class StreamChatMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        userMessage: String,
        sessionId: String,
        customInstruction: String? = null,
        onComplete: suspend (String) -> Unit
    ): Flow<String> {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = userMessage,
            role = MessageRole.USER,
            timestamp = Date()
        )
        chatRepository.saveMessage(userMsg)

        val fullResponse = StringBuilder()

        return chatRepository.streamMessage(userMessage, sessionId, customInstruction)
            .onEach { token -> fullResponse.append(token) }
            .onCompletion {
                val assistantMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = fullResponse.toString(),
                    role = MessageRole.ASSISTANT,
                    timestamp = Date()
                )
                chatRepository.saveMessage(assistantMsg)
                onComplete(fullResponse.toString())
            }
    }
}
