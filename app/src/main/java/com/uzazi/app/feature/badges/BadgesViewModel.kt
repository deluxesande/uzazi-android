package com.uzazi.app.feature.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzazi.app.core.data.UzaziDataStore
import com.uzazi.app.domain.models.Badge
import com.uzazi.app.domain.repositories.BadgeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BadgesViewModel @Inject constructor(
    private val badgeRepository: BadgeRepository,
    private val dataStore: UzaziDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(BadgesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        badgeRepository.getAllBadges().onEach { badges ->
            _uiState.update { it.copy(badges = badges) }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            dataStore.stats.collect { stats ->
                val newlyUnlocked = badgeRepository.checkAndUnlockBadges(stats)
                if (newlyUnlocked.isNotEmpty()) {
                    _uiState.update { it.copy(newlyUnlocked = newlyUnlocked) }
                }
            }
        }
    }

    fun dismissUnlockDialog() {
        _uiState.update { it.copy(newlyUnlocked = emptyList()) }
    }
}

data class BadgesUiState(
    val badges: List<Badge> = emptyList(),
    val newlyUnlocked: List<Badge> = emptyList()
)
