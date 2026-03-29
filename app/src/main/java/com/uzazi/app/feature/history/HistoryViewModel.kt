package com.uzazi.app.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzazi.app.domain.models.RiskAnalysisResult
import com.uzazi.app.domain.repositories.AiAnalysisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val aiRepository: AiAnalysisRepository
) : ViewModel() {

    val historyState: StateFlow<List<Pair<String, RiskAnalysisResult>>> = aiRepository.getAnalysisHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
