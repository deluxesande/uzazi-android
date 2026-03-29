package com.uzazi.app.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.domain.models.RiskAnalysisResult
import com.uzazi.app.domain.models.RiskLevel
import com.uzazi.app.domain.repositories.AiAnalysisRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAnalysisRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val secureStorage: SecureStorage
) : AiAnalysisRepository {

    private val COLLECTION = "ai_risk_analyses"

    override suspend fun saveAnalysis(checkInId: String, result: RiskAnalysisResult): Result<Unit> {
        val userId = secureStorage.getString(SecureStorage.KEY_USER_ID) ?: ""
        val data = hashMapOf(
            "checkInId" to checkInId,
            "userId" to userId,
            "riskLevel" to result.riskLevel.name,
            "clinicalSummary" to result.clinicalSummary,
            "timestamp" to System.currentTimeMillis()
        )
        return try {
            firestore.collection(COLLECTION).document(checkInId).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAnalysisHistory(): Flow<List<Pair<String, RiskAnalysisResult>>> = callbackFlow {
        val userId = secureStorage.getString(SecureStorage.KEY_USER_ID) ?: ""
        val listener = firestore.collection(COLLECTION)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val results = snapshot?.documents?.mapNotNull { doc ->
                    val checkInId = doc.getString("checkInId") ?: ""
                    val riskLevel = doc.getString("riskLevel")?.let { enumValueOf<RiskLevel>(it) } ?: RiskLevel.UNKNOWN
                    val summary = doc.getString("clinicalSummary") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    Triple(checkInId, RiskAnalysisResult(riskLevel, summary), timestamp)
                }?.sortedByDescending { it.third }
                ?.map { it.first to it.second } ?: emptyList()
                
                trySend(results)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getAnalysis(checkInId: String): RiskAnalysisResult? {
        return try {
            val doc = firestore.collection(COLLECTION).document(checkInId).get().await()
            if (doc.exists()) {
                val riskLevel = doc.getString("riskLevel")?.let { enumValueOf<RiskLevel>(it) } ?: RiskLevel.UNKNOWN
                val summary = doc.getString("clinicalSummary") ?: ""
                RiskAnalysisResult(riskLevel, summary)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
