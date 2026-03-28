package com.uzazi.app.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.uzazi.app.core.utils.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "uzazi_stats")

@Singleton
class UzaziDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val STREAK_COUNT = intPreferencesKey("streak_count")
    private val TOTAL_PETALS = intPreferencesKey("total_petals")
    private val LAST_CHECK_IN_DATE = longPreferencesKey("last_check_in_date")
    private val NIGHT_SESSION_COUNT = intPreferencesKey("night_session_count")
    private val SHARED_COUNT = intPreferencesKey("shared_count")
    private val SOUGHT_HELP_COUNT = intPreferencesKey("sought_help_count")
    private val COMEBACK_COUNT = intPreferencesKey("comeback_count")

    val stats: Flow<UserStats> = context.dataStore.data.map { prefs ->
        UserStats(
            streakCount = prefs[STREAK_COUNT] ?: 0,
            totalPetals = prefs[TOTAL_PETALS] ?: 0,
            nightSessionCount = prefs[NIGHT_SESSION_COUNT] ?: 0,
            sharedCount = prefs[SHARED_COUNT] ?: 0,
            soughtHelpCount = prefs[SOUGHT_HELP_COUNT] ?: 0,
            comebackCount = prefs[COMEBACK_COUNT] ?: 0,
            lastCheckInDate = prefs[LAST_CHECK_IN_DATE] ?: 0L
        )
    }

    suspend fun recordCheckIn(petalsEarned: Int): Boolean {
        var isComeback = false
        context.dataStore.edit { prefs ->
            val now = System.currentTimeMillis()
            val last = prefs[LAST_CHECK_IN_DATE] ?: 0L
            
            if (DateUtils.isToday(last)) {
                // Already checked in today, just add petals if needed (though usually 1/day)
                prefs[TOTAL_PETALS] = (prefs[TOTAL_PETALS] ?: 0) + petalsEarned
                return@edit
            }

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = calendar.timeInMillis

            if (DateUtils.isSameDay(last, yesterday)) {
                // Consecutive day
                prefs[STREAK_COUNT] = (prefs[STREAK_COUNT] ?: 0) + 1
            } else {
                // Gap
                if (last != 0L) {
                    prefs[COMEBACK_COUNT] = (prefs[COMEBACK_COUNT] ?: 0) + 1
                    isComeback = true
                }
                prefs[STREAK_COUNT] = 1
            }

            prefs[LAST_CHECK_IN_DATE] = now
            prefs[TOTAL_PETALS] = (prefs[TOTAL_PETALS] ?: 0) + petalsEarned + (if (isComeback) 2 else 0)
        }
        return isComeback
    }

    suspend fun recordNightSession() {
        context.dataStore.edit { prefs ->
            prefs[NIGHT_SESSION_COUNT] = (prefs[NIGHT_SESSION_COUNT] ?: 0) + 1
        }
    }

    suspend fun recordShare() {
        context.dataStore.edit { prefs ->
            prefs[SHARED_COUNT] = (prefs[SHARED_COUNT] ?: 0) + 1
        }
    }

    suspend fun recordSoughtHelp() {
        context.dataStore.edit { prefs ->
            prefs[SOUGHT_HELP_COUNT] = (prefs[SOUGHT_HELP_COUNT] ?: 0) + 1
        }
    }
}

data class UserStats(
    val streakCount: Int,
    val totalPetals: Int,
    val nightSessionCount: Int,
    val sharedCount: Int,
    val soughtHelpCount: Int,
    val comebackCount: Int,
    val lastCheckInDate: Long
)
