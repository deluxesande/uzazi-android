package com.uzazi.app.domain.repositories

import com.uzazi.app.domain.models.CheckIn
import com.uzazi.app.domain.models.RiskScore
import kotlinx.coroutines.flow.Flow

interface CheckInRepository {
    suspend fun submitCheckIn(
        mood: Int,
        symptoms: List<String>,
        connection: Int,
        sleep: Int,
        support: Int
    ): Flow<Result<String>>
    
    fun getHistory(): Flow<List<CheckIn>>
    
    suspend fun updateRiskLevel(id: String, riskLevel: String)

    suspend fun getRiskScore(checkInId: String): Flow<Result<RiskScore>>
}
