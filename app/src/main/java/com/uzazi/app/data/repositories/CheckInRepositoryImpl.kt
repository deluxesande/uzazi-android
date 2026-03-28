package com.uzazi.app.data.repositories

import com.uzazi.app.core.data.daos.CheckInDao
import com.uzazi.app.core.data.entities.CheckInEntity
import com.uzazi.app.core.network.ApiService
import com.uzazi.app.core.network.CheckInRequest
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.domain.models.CheckIn
import com.uzazi.app.domain.models.RiskLevel
import com.uzazi.app.domain.models.RiskScore
import com.uzazi.app.domain.repositories.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val checkInDao: CheckInDao,
    private val secureStorage: SecureStorage
) : CheckInRepository {

    override suspend fun submitCheckIn(
        mood: Int,
        symptoms: List<String>,
        connection: Int,
        sleep: Int,
        support: Int
    ): Flow<Result<String>> = flow {
        val userId = secureStorage.getString(SecureStorage.KEY_USER_ID) ?: ""
        val id = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        
        val entity = CheckInEntity(
            id = id,
            userId = userId,
            mood = mood,
            symptoms = symptoms.joinToString(","),
            connection = connection,
            sleep = sleep,
            support = support,
            riskLevel = "UNKNOWN",
            timestamp = timestamp,
            isSynced = false
        )
        
        checkInDao.insertCheckIn(entity)
        
        try {
            val response = apiService.submitCheckIn(
                CheckInRequest(userId, mood, symptoms, connection, sleep, support, timestamp)
            )
            if (response.isSuccessful) {
                checkInDao.markSynced(id)
                emit(Result.success(id))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e)) // Keep local copy, mark unsynced
        }
    }

    override fun getHistory(): Flow<List<CheckIn>> {
        return checkInDao.getAll().map { entities ->
            entities.map { entity ->
                CheckIn(
                    id = entity.id,
                    userId = entity.userId,
                    date = Date(entity.timestamp),
                    moodEmoji = mapMoodToEmoji(entity.mood),
                    physicalSymptoms = entity.symptoms.split(","),
                    sleepHours = entity.sleep,
                    hydrationGlasses = 0, // Placeholder
                    notes = null
                )
            }
        }
    }

    override suspend fun getRiskScore(checkInId: String): Flow<Result<RiskScore>> = flow {
        try {
            val response = apiService.getRiskScore(com.uzazi.app.core.network.RiskRequest(checkInId))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                emit(Result.success(RiskScore(
                    level = RiskLevel.valueOf(body.riskLevel.uppercase()),
                    score = body.score,
                    feedback = body.feedback
                )))
            } else {
                emit(Result.failure(Exception("API Error")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun mapMoodToEmoji(mood: Int): String = when (mood) {
        1 -> "😔"
        2 -> "😐"
        3 -> "🙂"
        4 -> "😊"
        5 -> "🤩"
        else -> "🤔"
    }
}
