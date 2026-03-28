package com.uzazi.app.feature.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.ui.components.MamaBearAvatar
import com.uzazi.app.ui.theme.BloomPink
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    secureStorage: SecureStorage,
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    LaunchedEffect(Unit) {
        val onboardingComplete = secureStorage.getBoolean(SecureStorage.KEY_ONBOARDING_COMPLETE)
        val userId = secureStorage.getString(SecureStorage.KEY_USER_ID)
        
        delay(1500)
        
        when {
            onboardingComplete && userId != null -> onNavigateToHome()
            !onboardingComplete -> onNavigateToOnboarding()
            else -> onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MamaBearAvatar(size = 120.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Uzazi",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = BloomPink
            )
        }
    }
}
