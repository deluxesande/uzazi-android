package com.uzazi.app.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uzazi.app.core.data.UzaziDataStore
import com.uzazi.app.core.network.AbsenceRequest
import com.uzazi.app.core.network.ApiService
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.core.utils.DateUtils
import com.uzazi.app.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class AbsenceCheckerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val dataStore: UzaziDataStore,
    private val apiService: ApiService,
    private val secureStorage: SecureStorage
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val stats = dataStore.stats.first()
        val lastCheckIn = stats.lastCheckInDate
        if (lastCheckIn == 0L) return Result.success()

        val diff = System.currentTimeMillis() - lastCheckIn
        val daysAbsent = TimeUnit.MILLISECONDS.toDays(diff).toInt()

        if (daysAbsent >= 1) {
            NotificationHelper.showAbsenceNudge(applicationContext, daysAbsent)
            
            val userId = secureStorage.getString(SecureStorage.KEY_USER_ID)
            if (userId != null && daysAbsent >= 2) {
                try {
                    apiService.flagAbsence(AbsenceRequest(userId, lastCheckIn))
                } catch (e: Exception) {
                    // Ignore network failures for background flagging
                }
            }
        }

        return Result.success()
    }
}
