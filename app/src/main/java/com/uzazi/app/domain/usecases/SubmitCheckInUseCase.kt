package com.uzazi.app.domain.usecases

import com.uzazi.app.core.ai.AiCheckInRiskAnalyzerService
import com.uzazi.app.domain.models.RiskLevel
import com.uzazi.app.domain.models.RiskScore
import com.uzazi.app.domain.repositories.AiAnalysisRepository
import com.uzazi.app.domain.repositories.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubmitCheckInUseCase @Inject constructor(
    private val repository: CheckInRepository,
    private val aiAnalyzer: AiCheckInRiskAnalyzerService,
    private val aiRepository: AiAnalysisRepository
) {
    suspend operator fun invoke(
        mood: Int,
        symptoms: List<String>,
        connection: Int,
        sleep: Int,
        support: Int
    ): Flow<Result<RiskScore>> = flow {
        repository.submitCheckIn(mood, symptoms, connection, sleep, support).collect { result ->
            result.onSuccess { id ->
                // Step 2: Run AI Analysis
                val answers = mutableListOf<String>().apply {
                    add("Mood: $mood/5")
                    add("Symptoms: ${symptoms.joinToString(", ")}")
                    add("Connection with baby: $connection/5")
                    add("Sleep quality: $sleep/5")
                    add("Social support: $support/5")
                }
                
                val aiResult = aiAnalyzer.analyzeCheckIn(answers)
                
                // Step 3: Store AI Analysis
                aiRepository.saveAnalysis(id, aiResult)
                repository.updateRiskLevel(id, aiResult.riskLevel.name)
                
                // Step 4: Emit combined result
                emit(Result.success(RiskScore(
                    level = aiResult.riskLevel,
                    score = when(aiResult.riskLevel) {
                        RiskLevel.LOW -> 20
                        RiskLevel.MEDIUM -> 50
                        RiskLevel.HIGH -> 85
                        else -> 0
                    },
                    feedback = aiResult.clinicalSummary
                )))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }
}
