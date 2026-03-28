package com.uzazi.app.domain.usecases

import com.uzazi.app.domain.models.RiskScore
import com.uzazi.app.domain.repositories.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubmitCheckInUseCase @Inject constructor(
    private val repository: CheckInRepository
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
                repository.getRiskScore(id).collect { riskResult ->
                    emit(riskResult)
                }
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }
}
