package com.uzazi.app.navigation

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object Onboarding : NavRoutes("onboarding")
    object Auth : NavRoutes("auth")
    object Home : NavRoutes("home")
    object CheckIn : NavRoutes("check_in")
    object Result : NavRoutes("result")
    object NightCompanion : NavRoutes("night_companion")
    object Badges : NavRoutes("badges")
    object Share : NavRoutes("share")
}
