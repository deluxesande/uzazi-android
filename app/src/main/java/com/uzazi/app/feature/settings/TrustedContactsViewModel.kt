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
class TrustedContactsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrustedContactsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TrustedContactsEvent>()
    val events = _events.asSharedFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val user = userRepository.getCurrentUser()
        _uiState.update { it.copy(user = user, phoneInput = user?.trustedContactPhone ?: "") }
    }

    fun updatePhone(phone: String) {
        _uiState.update { it.copy(phoneInput = phone) }
    }

    fun saveContact() {
        val currentUser = _uiState.value.user ?: return
        val updatedUser = currentUser.copy(trustedContactPhone = _uiState.value.phoneInput)
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.saveUserProfile(updatedUser).collect { result ->
                result.onSuccess {
                    _uiState.update { it.copy(isLoading = false, user = updatedUser) }
                    _events.emit(TrustedContactsEvent.SaveSuccess)
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }
}

data class TrustedContactsUiState(
    val user: User? = null,
    val phoneInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class TrustedContactsEvent {
    object SaveSuccess : TrustedContactsEvent()
}
