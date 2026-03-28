package com.uzazi.app.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uzazi.app.core.data.daos.CheckInDao
import com.uzazi.app.core.network.ApiService
import com.uzazi.app.core.network.CheckInRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncCheckInWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val checkInDao: CheckInDao,
    private val apiService: ApiService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val unsynced = checkInDao.getUnsynced()
        if (unsynced.isEmpty()) return Result.success()

        var allSuccess = true
        for (entity in unsynced) {
            try {
                val response = apiService.submitCheckIn(
                    CheckInRequest(
                        userId = entity.userId,
                        mood = entity.mood,
                        symptoms = entity.symptoms.split(","),
                        connection = entity.connection,
                        sleep = entity.sleep,
                        support = entity.support,
                        timestamp = entity.timestamp
                    )
                )
                if (response.isSuccessful) {
                    checkInDao.markSynced(entity.id)
                } else {
                    allSuccess = false
                }
            } catch (e: Exception) {
                allSuccess = false
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }
}
