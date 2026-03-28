package com.uzazi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.uzazi.app.core.security.SecureStorage
import com.uzazi.app.navigation.BottomNavBar
import com.uzazi.app.navigation.NavRoutes
import com.uzazi.app.navigation.UzaziNavGraph
import com.uzazi.app.ui.theme.UzaziTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var secureStorage: SecureStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isNightMode = remember {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                hour !in 5..<22
            }

            UzaziTheme(nightMode = isNightMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                val showBottomBar = currentRoute !in listOf(
                    NavRoutes.Splash.route,
                    NavRoutes.Onboarding.route,
                    NavRoutes.Auth.route
                )

                Scaffold(
                    bottomBar = { if (showBottomBar) BottomNavBar(navController) }
                ) { innerPadding ->
                    UzaziNavGraph(
                        navController = navController,
                        secureStorage = secureStorage
                    )
                }
            }
        }
    }
}
