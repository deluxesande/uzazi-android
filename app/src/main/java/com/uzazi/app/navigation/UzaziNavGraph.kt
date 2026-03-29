package com.uzazi.app.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.feature.auth.AuthScreen
import com.uzazi.app.feature.onboarding.*
import com.uzazi.app.feature.home.HomeScreen
import com.uzazi.app.feature.home.HomeViewModel
import com.uzazi.app.feature.history.HistoryScreen
import com.uzazi.app.feature.checkin.CheckInScreen
import com.uzazi.app.feature.checkin.ResultScreen
import com.uzazi.app.feature.nightcompanion.NightCompanionScreen
import com.uzazi.app.feature.share.ShareScreen
import com.uzazi.app.feature.share.QrCodeScreen
import com.uzazi.app.feature.settings.SettingsScreen
import com.uzazi.app.feature.settings.TrustedContactsScreen
import com.uzazi.app.domain.models.RiskLevel

@Composable
fun UzaziNavGraph(
    navController: NavHostController,
    secureStorage: SecureStorage,
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    NavHost(navController = navController, startDestination = NavRoutes.Splash.route) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(
                secureStorage = secureStorage,
                onNavigateToHome = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(NavRoutes.Onboarding.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(NavRoutes.Auth.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Onboarding.route) {
            val uiState by onboardingViewModel.uiState.collectAsState()

            if (uiState.isComplete) {
                navController.navigate(NavRoutes.Auth.route) {
                    popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                }
            }

            Crossfade(targetState = uiState.currentStep, label = "onboarding_step") { step ->
                when (step) {
                    OnboardingStep.WELCOME -> WelcomeScreen(onNext = onboardingViewModel::nextStep)
                    OnboardingStep.LANGUAGE -> LanguageSelectScreen(onLanguageSelected = onboardingViewModel::saveLanguage)
                    OnboardingStep.DELIVERY_DATE -> DeliveryDateScreen(onDateSelected = onboardingViewModel::saveDeliveryDate)
                    OnboardingStep.TRUSTED_CONTACT -> TrustedContactScreen(
                        onComplete = onboardingViewModel::saveTrustedContact,
                        onSkip = onboardingViewModel::completeOnboarding
                    )
                }
            }
        }

        composable(NavRoutes.Auth.route) {
            AuthScreen(
                onAuthComplete = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val uiState by homeViewModel.uiState.collectAsState()
            
            HomeScreen(
                onNavigateToCheckIn = { navController.navigate(NavRoutes.CheckIn.route) },
                onNavigateToResult = { 
                    navController.navigate("${NavRoutes.Result.route}/${uiState.lastRiskLevel.name}")
                },
                onNavigateToCompanion = { navController.navigate(NavRoutes.NightCompanion.route) },
                onNavigateToSettings = { navController.navigate(NavRoutes.Settings.route) },
                onNavigateToTrustedContacts = { navController.navigate(NavRoutes.TrustedContacts.route) },
                onNavigateToHistory = { navController.navigate(NavRoutes.History.route) },
                viewModel = homeViewModel
            )
        }

        composable(NavRoutes.CheckIn.route, deepLinks = listOf(navDeepLink { uriPattern = "uzazi://open/checkin" })) {
            CheckInScreen(
                onNavigateToResult = { riskLevel ->
                    navController.navigate("${NavRoutes.Result.route}/$riskLevel") {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable("${NavRoutes.Result.route}/{riskLevel}") { backStackEntry ->
            val riskLevel = backStackEntry.arguments?.getString("riskLevel") ?: "UNKNOWN"
            ResultScreen(
                riskLevelStr = riskLevel,
                onBackToGarden = {
                    navController.popBackStack(NavRoutes.Home.route, inclusive = false)
                }
            )
        }

        composable(NavRoutes.NightCompanion.route, deepLinks = listOf(navDeepLink { uriPattern = "uzazi://open/night" })) {
            NightCompanionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Badges.route, deepLinks = listOf(navDeepLink { uriPattern = "uzazi://open/badges" })) {
            com.uzazi.app.feature.badges.BadgesScreen()
        }

        composable(NavRoutes.Share.route, deepLinks = listOf(navDeepLink { uriPattern = "uzazi://open/share" })) {
            ShareScreen(
                onNavigateToQr = { token, expiry ->
                    navController.navigate("qr/$token/$expiry")
                }
            )
        }

        composable(NavRoutes.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(NavRoutes.Auth.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.TrustedContacts.route) {
            TrustedContactsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("qr/{token}/{expiry}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            val expiry = backStackEntry.arguments?.getString("expiry")?.toLong() ?: 0L
            QrCodeScreen(
                qrToken = token,
                expiry = expiry,
                onBack = { navController.popBackStack() },
                onRefresh = { /* ViewModel refresh call */ }
            )
        }
    }
}
