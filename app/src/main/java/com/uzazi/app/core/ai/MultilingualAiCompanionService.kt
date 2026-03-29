package com.uzazi.app.core.ai

import com.google.cloud.translate.v3.LocationName
import com.google.cloud.translate.v3.TranslateTextRequest
import com.google.cloud.translate.v3.TranslationServiceClient
import com.google.cloud.vertexai.VertexAI
import com.google.cloud.vertexai.generativeai.ContentMaker
import com.google.cloud.vertexai.generativeai.GenerativeModel
import com.google.cloud.vertexai.generativeai.ResponseHandler
import com.uzazi.app.domain.models.ChatMessage
import com.uzazi.app.domain.models.MessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultilingualAiCompanionService @Inject constructor(
    private val vertexAI: VertexAI,
    private val translationServiceClient: TranslationServiceClient,
    private val projectId: String // Inject GCP project ID
) {
    private val modelName = "gemini-1.5-flash"
    private val location = "us-central1"

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
        
        // Step A: Translate to English if needed
        val englishUserMessage = if (targetLanguageCode != "en") {
            translateText(userMessage, "en", targetLanguageCode)
        } else {
            userMessage
        }

        // Step B: Call Gemini with history
        val generativeModel = GenerativeModel(modelName, vertexAI)
        
        // Add history and system instruction
        val chatHistory = mutableListOf<com.google.cloud.vertexai.api.Content>()
        
        // Prepend system instruction to history
        chatHistory.add(ContentMaker.forRole("user").fromString("SYSTEM INSTRUCTION: $systemInstruction"))
        chatHistory.add(ContentMaker.forRole("model").fromString("Understood. I will act as Mama Bear, the empathetic companion for Uzazi users."))

        history.forEach { msg ->
            val role = when(msg.role) {
                MessageRole.USER -> "user"
                MessageRole.ASSISTANT -> "model"
                else -> "user"
            }
            chatHistory.add(ContentMaker.forRole(role).fromString(msg.text))
        }

        val chat = generativeModel.startChat()
        chat.setHistory(chatHistory)

        // Send current message
        val response = chat.sendMessage(englishUserMessage)
        val englishAiResponse = ResponseHandler.getText(response)

        // Step C: Translate back to target language if needed
        if (targetLanguageCode != "en") {
            translateText(englishAiResponse, targetLanguageCode, "en")
        } else {
            englishAiResponse
        }
    }

    private fun translateText(text: String, targetLanguage: String, sourceLanguage: String? = null): String {
        val parent = LocationName.of(projectId, "global").toString()
        
        val request = TranslateTextRequest.newBuilder()
            .setParent(parent)
            .setMimeType("text/plain")
            .setTargetLanguageCode(targetLanguage)
            .apply { if (sourceLanguage != null) sourceLanguageCode = sourceLanguage }
            .addContents(text)
            .build()

        val response = translationServiceClient.translateText(request)
        return response.getTranslations(0).translatedText
    }
}
