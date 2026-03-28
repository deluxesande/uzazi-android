package com.uzazi.app.feature.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzazi.app.domain.models.RiskLevel
import com.uzazi.app.domain.models.RiskScore
import com.uzazi.app.domain.usecases.SubmitCheckInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val submitCheckInUseCase: SubmitCheckInUseCase,
    private val dataStore: com.uzazi.app.core.data.UzaziDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState = _uiState.asStateFlow()

    // Question pool to keep things fresh
    private val allQuestions = listOf(
        Question("How have you been feeling emotionally today?", listOf("Very Sad", "A bit low", "Okay", "Happy", "Wonderful")), // CORE
        Question("Any physical pain or discomfort?", listOf("Severe", "Moderate", "Mild", "None")), // CORE
        Question("Do you feel supported by those around you?", listOf("Not at all", "Somewhat", "Mostly", "Fully")), // CORE
        Question("How many hours did you sleep last night?", listOf("0-2", "3-4", "5-6", "7+")),
        Question("How connected do you feel to your baby?", listOf("Not at all", "Trying", "A little", "Very much")),
        Question("How much water have you had today?", listOf("None", "1-3 glasses", "4-6 glasses", "8+ glasses")),
        Question("Have you had time for a small walk or fresh air?", listOf("No", "I tried", "Yes, a little", "Yes, plenty")),
        Question("How is your appetite today, mama?", listOf("Very poor", "Poor", "Okay", "Good"))
    )

    // Select 5 questions: First 3 are always core, last 2 rotate based on the day of the year
    val questions: List<Question> by lazy {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val core = allQuestions.take(3)
        val others = allQuestions.drop(3)
        val rotated = listOf(
            others[dayOfYear % others.size],
            others[(dayOfYear + 1) % others.size]
        )
        core + rotated
    }

    fun selectAnswer(questionIndex: Int, answerIndex: Int) {
        val newAnswers = _uiState.value.answers.toMutableMap()
        newAnswers[questionIndex] = answerIndex
        _uiState.update { it.copy(answers = newAnswers) }
        
        viewModelScope.launch {
            delay(450)
            if (questionIndex == _uiState.value.currentQuestionIndex) {
                nextQuestion()
            }
        }
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
                mood = answers[0] ?: 2,
                symptoms = listOf(questions[1].options[answers[1] ?: 0]),
                connection = answers[2] ?: 2,
                sleep = answers[3] ?: 2,
                support = answers[4] ?: 2
            ).collect { result ->
                result.onSuccess { riskScore ->
                    dataStore.recordCheckIn(petalsEarned = 1)
                    _uiState.update { it.copy(isSubmitting = false, riskResult = riskScore) }
                }.onFailure {
                    // FALLBACK: Calculate risk locally if API fails so user isn't stuck
                    val fallbackScore = calculateLocalRisk(answers)
                    dataStore.recordCheckIn(petalsEarned = 1)
                    _uiState.update { it.copy(isSubmitting = false, riskResult = fallbackScore) }
                }
            }
        }
    }

    private fun calculateLocalRisk(answers: Map<Int, Int>): RiskScore {
        // Very simple logic for demo fallback
        val mood = answers[0] ?: 2
        val physical = answers[1] ?: 3
        
        return when {
            mood <= 1 || physical == 0 -> RiskScore(RiskLevel.HIGH, 80, "Please reach out to a professional.")
            mood == 2 || physical == 1 -> RiskScore(RiskLevel.MEDIUM, 40, "Take it easy today, mama.")
            else -> RiskScore(RiskLevel.LOW, 10, "You're doing great!")
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
