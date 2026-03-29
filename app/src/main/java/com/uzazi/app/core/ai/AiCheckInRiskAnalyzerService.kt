package com.uzazi.app.core.ai

import com.google.cloud.vertexai.VertexAI
import com.google.cloud.vertexai.generativeai.GenerativeModel
import com.google.cloud.vertexai.generativeai.ResponseHandler
import com.google.gson.Gson
import com.uzazi.app.domain.models.RiskAnalysisResult
import com.uzazi.app.domain.models.RiskLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiCheckInRiskAnalyzerService @Inject constructor(
    private val vertexAI: VertexAI,
    private val gson: Gson
) {
    private val modelName = "gemini-1.5-flash"

    suspend fun analyzeCheckIn(answers: List<String>): RiskAnalysisResult = withContext(Dispatchers.IO) {
        val generativeModel = GenerativeModel(modelName, vertexAI)
        
        val prompt = """
            You are a clinical postpartum expert. Analyze the following answers from a mother's daily check-in:
            ${answers.joinToString("\n- ")}
            
            Evaluate her emotional and physical state. Return your analysis ONLY as a JSON object with:
            - "riskLevel": One of "LOW", "MEDIUM", or "HIGH"
            - "clinicalSummary": A brief summary of her state and any areas of concern.
            
            Strictly follow this JSON format.
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)
        val responseText = ResponseHandler.getText(response)
        
        // Extract JSON if the model includes markdown formatting
        val jsonText = responseText.substringAfter("```json").substringBefore("```").trim().ifEmpty { 
            responseText.trim() 
        }

        try {
            val rawResult = gson.fromJson(jsonText, RawRiskResult::class.java)
            RiskAnalysisResult(
                riskLevel = enumValueOf<RiskLevel>(rawResult.riskLevel.uppercase()),
                clinicalSummary = rawResult.clinicalSummary
            )
        } catch (e: Exception) {
            RiskAnalysisResult(
                riskLevel = RiskLevel.UNKNOWN,
                clinicalSummary = "Analysis failed: ${e.message}"
            )
        }
    }

    private data class RawRiskResult(
        val riskLevel: String,
        val clinicalSummary: String
    )
}
