package com.uzazi.app.domain.repositories

import com.uzazi.app.domain.models.RiskAnalysisResult
import kotlinx.coroutines.flow.Flow

interface AiAnalysisRepository {
    suspend fun saveAnalysis(checkInId: String, result: RiskAnalysisResult): Result<Unit>
    fun getAnalysisHistory(): Flow<List<Pair<String, RiskAnalysisResult>>>
    suspend fun getAnalysis(checkInId: String): RiskAnalysisResult?
}
