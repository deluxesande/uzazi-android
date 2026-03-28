package com.uzazi.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.uzazi.app.notifications.NotificationHelper
import com.uzazi.app.workers.AbsenceCheckerWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class UzaziApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
        setupPeriodicWorkers()
    }

    private fun setupPeriodicWorkers() {
        val absenceRequest = PeriodicWorkRequestBuilder<AbsenceCheckerWorker>(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AbsenceChecker",
            ExistingPeriodicWorkPolicy.KEEP,
            absenceRequest
        )
    }
}
