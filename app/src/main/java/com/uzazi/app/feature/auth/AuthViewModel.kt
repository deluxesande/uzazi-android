package com.uzazi.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzazi.app.domain.models.User
import com.uzazi.app.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    fun sendOtp(phone: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.signInWithPhone(phone).collect { result ->
                result.onSuccess { id ->
                    _uiState.update { it.copy(isLoading = false, step = AuthStep.OTP_VERIFY, verificationId = id) }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    fun verifyOtp(otp: String) {
        val verificationId = _uiState.value.verificationId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.verifyOtp(verificationId, otp).collect { result ->
                result.onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, step = AuthStep.COMPLETE, user = user) }
                    _events.emit(AuthEvent.AuthComplete)
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.signInWithGoogle(idToken).collect { result ->
                result.onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, step = AuthStep.COMPLETE, user = user) }
                    _events.emit(AuthEvent.AuthComplete)
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    fun backToPhoneInput() {
        _uiState.update { it.copy(step = AuthStep.PHONE_INPUT, error = null) }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val step: AuthStep = AuthStep.PHONE_INPUT,
    val verificationId: String? = null,
    val user: User? = null
)

enum class AuthStep {
    PHONE_INPUT, OTP_VERIFY, COMPLETE
}

sealed class AuthEvent {
    object AuthComplete : AuthEvent()
}
