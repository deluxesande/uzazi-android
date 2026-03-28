package com.uzazi.app.feature.onboarding

import androidx.lifecycle.ViewModel
import com.uzazi.app.core.security.SecureStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val secureStorage: SecureStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    fun nextStep() {
        val currentStepIndex = OnboardingStep.values().indexOf(_uiState.value.currentStep)
        if (currentStepIndex < OnboardingStep.values().size - 1) {
            _uiState.update { it.copy(currentStep = OnboardingStep.values()[currentStepIndex + 1]) }
        }
    }

    fun previousStep() {
        val currentStepIndex = OnboardingStep.values().indexOf(_uiState.value.currentStep)
        if (currentStepIndex > 0) {
            _uiState.update { it.copy(currentStep = OnboardingStep.values()[currentStepIndex - 1]) }
        }
    }

    fun saveLanguage(lang: String) {
        secureStorage.saveString(SecureStorage.KEY_LANGUAGE, lang)
        nextStep()
    }

    fun saveDeliveryDate(date: Long) {
        secureStorage.saveLong(SecureStorage.KEY_DELIVERY_DATE, date)
        nextStep()
    }

    fun saveTrustedContact(phone: String) {
        secureStorage.saveString(SecureStorage.KEY_TRUSTED_CONTACT, phone)
        completeOnboarding()
    }

    fun completeOnboarding() {
        secureStorage.saveBoolean(SecureStorage.KEY_ONBOARDING_COMPLETE, true)
        _uiState.update { it.copy(isComplete = true) }
    }
}

data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val isComplete: Boolean = false
)

enum class OnboardingStep {
    WELCOME, LANGUAGE, DELIVERY_DATE, TRUSTED_CONTACT
}
