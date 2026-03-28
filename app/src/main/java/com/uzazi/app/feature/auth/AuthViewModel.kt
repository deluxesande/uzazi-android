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

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.signInWithEmail(email, pass).collect { result ->
                result.onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, user = user) }
                    _events.emit(AuthEvent.AuthComplete)
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    fun register(email: String, pass: String, name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            userRepository.signUpWithEmail(email, pass, name).collect { result ->
                result.onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, user = user) }
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
                    _uiState.update { it.copy(isLoading = false, user = user) }
                    _events.emit(AuthEvent.AuthComplete)
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    fun toggleAuthMode() {
        _uiState.update { 
            it.copy(
                isRegistration = !it.isRegistration,
                error = null 
            ) 
        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isRegistration: Boolean = false
)

sealed class AuthEvent {
    object AuthComplete : AuthEvent()
}
