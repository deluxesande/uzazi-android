package com.uzazi.app.feature.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.uzazi.app.core.data.UzaziDataStore
import com.uzazi.app.core.data.daos.CheckInDao
import com.uzazi.app.core.network.ApiService
import com.uzazi.app.core.network.QrRequest
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.core.utils.PostpartumDayCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val checkInDao: CheckInDao,
    private val secureStorage: SecureStorage,
    private val apiService: ApiService,
    private val dataStore: UzaziDataStore,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShareUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val userId = secureStorage.getString(SecureStorage.KEY_USER_ID) ?: return
        val deliveryDate = secureStorage.getLong(SecureStorage.KEY_DELIVERY_DATE)
        val day = PostpartumDayCalculator.calculate(deliveryDate)

        viewModelScope.launch {
            val latestCheckIn = checkInDao.getLatest().first()
            val stats = dataStore.stats.first()
            
            // Load CHW info from Firestore
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    val chwName = doc.getString("chwName") ?: "My Health Worker"
                    val chwPhone = doc.getString("chwPhone") ?: ""
                    _uiState.update { it.copy(chwName = chwName, chwPhone = chwPhone) }
                }

            val riskText = latestCheckIn?.riskLevel ?: "Stable"
            val summary = """
                Hi, this is an anonymous Uzazi user on day $day of my postpartum journey.
                My wellness check-in today: $riskText.
                Streak: ${stats.streakCount} days. Petals earned: ${stats.totalPetals}.
                Could we arrange a check-in this week? Thank you.
            """.trimIndent()

            _uiState.update { it.copy(
                summaryText = summary,
                lastRiskLevel = riskText,
                isLoading = false
            ) }
        }
    }

    fun generateQrToken() {
        val userId = secureStorage.getString(SecureStorage.KEY_USER_ID) ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.generateQr(QrRequest(userId))
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(
                        qrToken = response.body()!!.qrCodeBase64,
                        qrExpiry = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class ShareUiState(
    val summaryText: String = "",
    val qrToken: String? = null,
    val qrExpiry: Long = 0,
    val isLoading: Boolean = true,
    val lastRiskLevel: String = "",
    val chwName: String = "My Health Worker",
    val chwPhone: String = "",
    val shareComplete: Boolean = false
)
