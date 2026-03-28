package com.uzazi.app.feature.auth

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun AuthScreen(
    navController: NavHostController,
    onAuthComplete: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is AuthEvent.AuthComplete) {
                onAuthComplete()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(targetState = uiState.step, label = "auth_step") { step ->
            when (step) {
                AuthStep.PHONE_INPUT -> PhoneInputScreen(
                    onSendOtp = viewModel::sendOtp,
                    onGoogleSignIn = viewModel::signInWithGoogle,
                    error = uiState.error
                )
                AuthStep.OTP_VERIFY -> OtpVerifyScreen(
                    onVerifyOtp = viewModel::verifyOtp,
                    onBack = viewModel::backToPhoneInput,
                    error = uiState.error
                )
                AuthStep.COMPLETE -> { /* Navigation handled by LaunchedEffect */ }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
