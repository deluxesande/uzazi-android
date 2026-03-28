package com.uzazi.app.feature.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzazi.app.domain.models.RiskScore
import com.uzazi.app.domain.usecases.SubmitCheckInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val submitCheckInUseCase: SubmitCheckInUseCase,
    private val dataStore: com.uzazi.app.core.data.UzaziDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState = _uiState.asStateFlow()

    val questions = listOf(
        Question("How have you been feeling emotionally today?", listOf("Very Sad", "A bit low", "Okay", "Happy", "Wonderful")),
        Question("Any physical pain or discomfort?", listOf("Severe", "Moderate", "Mild", "None")),
        Question("How connected do you feel to your baby?", listOf("Not at all", "Trying", "A little", "Very much")),
        Question("How many hours did you sleep last night?", listOf("0-2", "3-4", "5-6", "7+")),
        Question("Do you feel supported by those around you?", listOf("Not at all", "Somewhat", "Mostly", "Fully"))
    )

    fun selectAnswer(questionIndex: Int, answerIndex: Int) {
        val newAnswers = _uiState.value.answers.toMutableMap()
        newAnswers[questionIndex] = answerIndex
        _uiState.update { it.copy(answers = newAnswers) }
    }

    fun nextQuestion() {
        if (_uiState.value.currentQuestionIndex < questions.size - 1) {
            _uiState.update { it.copy(currentQuestionIndex = it.currentQuestionIndex + 1) }
        } else {
            submitCheckIn()
        }
    }

    private fun submitCheckIn() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val answers = _uiState.value.answers
            submitCheckInUseCase(
                mood = answers[0] ?: 0,
                symptoms = listOf(questions[1].options[answers[1] ?: 0]),
                connection = answers[2] ?: 0,
                sleep = answers[3] ?: 0,
                support = answers[4] ?: 0
            ).collect { result ->
                result.onSuccess { riskScore ->
                    _uiState.update { it.copy(isSubmitting = false, riskResult = riskScore) }
                }.onFailure {
                    _uiState.update { it.copy(isSubmitting = false) }
                }
            }
        }
    }
}

data class CheckInUiState(
    val currentQuestionIndex: Int = 0,
    val answers: Map<Int, Int> = emptyMap(),
    val isSubmitting: Boolean = false,
    val riskResult: RiskScore? = null
)

data class Question(val text: String, val options: List<String>)
