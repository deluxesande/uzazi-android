package com.uzazi.app.core.ai

import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.content
import com.uzazi.app.domain.models.ChatMessage
import com.uzazi.app.domain.models.MessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MultilingualAiCompanionService @Inject constructor(
    @Named("companion") private val generativeModel: GenerativeModel
) {
    private val systemInstruction = """
        You are "Mama Bear", a warm, empathetic postpartum companion for new mothers in East Africa.
        Your tone is gentle, supportive, and reassuring.
        IMPORTANT DISCLAIMER: You are an AI, not a doctor. You provide NO medical advice. 
        Always encourage the user to seek professional medical help for clinical concerns.
    """.trimIndent()

    suspend fun getCompanionResponse(
        userMessage: String,
        history: List<ChatMessage>,
        targetLanguageCode: String
    ): String = withContext(Dispatchers.IO) {
        
        // Note: Translation is temporarily disabled to resolve Protobuf conflicts.
        // We recommend using ML Kit Translation or REST API for production.
        val englishUserMessage = userMessage 

        val chatHistory = history.map { msg ->
            content(role = if (msg.role == MessageRole.USER) "user" else "model") {
                text(msg.text)
            }
        }

        val chat = generativeModel.startChat(
            history = listOf(
                content(role = "user") { text("SYSTEM INSTRUCTION: $systemInstruction") },
                content(role = "model") { text("Understood. I will act as Mama Bear, the empathetic companion for Uzazi users.") }
            ) + chatHistory
        )

        val response = chat.sendMessage(englishUserMessage)
        response.text ?: ""
    }
}
