package com.uzazi.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzazi.app.core.data.UzaziDataStore
import com.uzazi.app.core.data.daos.BadgeDao
import com.uzazi.app.core.data.daos.CheckInDao
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.core.utils.NightModeDetector
import com.uzazi.app.core.utils.PostpartumDayCalculator
import com.uzazi.app.domain.models.RiskLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val secureStorage: SecureStorage,
    private val checkInDao: CheckInDao,
    private val badgeDao: BadgeDao,
    private val dataStore: UzaziDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val name = secureStorage.getString(SecureStorage.KEY_USER_ID) ?: "Mama"
        val deliveryDate = secureStorage.getLong(SecureStorage.KEY_DELIVERY_DATE)
        val day = PostpartumDayCalculator.calculate(deliveryDate)
        
        combine(
            checkInDao.getLatest(),
            badgeDao.getAll(),
            dataStore.stats
        ) { latestCheckIn, badges, stats ->
            val todayCheckedIn = latestCheckIn?.let { 
                com.uzazi.app.core.utils.DateUtils.isToday(it.timestamp) 
            } ?: false
            
            HomeUiState(
                userName = name,
                postpartumDay = day,
                streakCount = stats.streakCount,
                petalCount = stats.totalPetals % 31, // Garden fills at 30
                lastRiskLevel = latestCheckIn?.riskLevel?.let { RiskLevel.valueOf(it) } ?: RiskLevel.UNKNOWN,
                todayCheckedIn = todayCheckedIn,
                badgesEarned = badges.count { it.unlockedAt != null },
                showComebackMessage = stats.comebackCount > 0 && !todayCheckedIn // Simple logic for demo
            )
        }.onEach { state ->
            _uiState.update { state }
        }.launchIn(viewModelScope)
    }

    fun dismissComebackMessage() {
        _uiState.update { it.copy(showComebackMessage = false) }
    }
}

data class HomeUiState(
    val userName: String = "",
    val postpartumDay: Int = 1,
    val streakCount: Int = 0,
    val petalCount: Int = 0,
    val lastRiskLevel: RiskLevel = RiskLevel.UNKNOWN,
    val todayCheckedIn: Boolean = false,
    val badgesEarned: Int = 0,
    val showComebackMessage: Boolean = false
)
