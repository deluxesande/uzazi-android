package com.uzazi.app.feature.settings

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
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events = _events.asSharedFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val user = userRepository.getCurrentUser()
        _uiState.update { it.copy(user = user, nameInput = user?.name ?: "") }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(nameInput = name) }
    }

    fun saveProfile() {
        val currentUser = _uiState.value.user ?: return
        val updatedUser = currentUser.copy(name = _uiState.value.nameInput)
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.saveUserProfile(updatedUser).collect { result ->
                result.onSuccess {
                    _uiState.update { it.copy(isLoading = false, user = updatedUser) }
                    _events.emit(SettingsEvent.SaveSuccess)
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
            _events.emit(SettingsEvent.SignOutSuccess)
        }
    }
}

data class SettingsUiState(
    val user: User? = null,
    val nameInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SettingsEvent {
    object SaveSuccess : SettingsEvent()
    object SignOutSuccess : SettingsEvent()
}
