package com.jetbrains.inlock

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetbrains.inlock.data.FirebaseService
import com.jetbrains.inlock.navigation.AppDestination
import com.jetbrains.inlock.screens.assets.AssetsScreen
import com.jetbrains.inlock.screens.auth.LoginScreen
import com.jetbrains.inlock.screens.detail.AssetDetailScreen
import com.jetbrains.inlock.screens.profile.ProfileScreen
import com.jetbrains.inlock.ui.theme.InLockTheme
import com.jetbrains.inlock.ui.theme.inLockDarkColorScheme
import com.jetbrains.inlock.ui.theme.inLockLightColorScheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val firebaseService = koinInject<FirebaseService>()
    var isSignedIn by remember { mutableStateOf(false) }
    var isInitialLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isSignedIn = firebaseService.isUserSignedIn()
        isInitialLoading = false
    }

    InLockTheme {
        Surface {
            if (isInitialLoading) {
                SplashScreen()
            } else {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    startDestination = if (isSignedIn) AppDestination.ASSETS else AppDestination.LOGIN
                )
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Surface(color = MaterialTheme.colorScheme.primary) {
        // Simple splash content
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppDestination.LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(AppDestination.ASSETS) {
                    popUpTo(AppDestination.LOGIN) { inclusive = true }
                }
            })
        }
        composable(AppDestination.ASSETS) {
            AssetsScreen(
                navigateToDetails = { assetId ->
                    navController.navigate("${AppDestination.ASSET_DETAIL}/$assetId")
                },
                navigateToProfile = {
                    navController.navigate(AppDestination.PROFILE)
                }
            )
        }
        composable("${AppDestination.ASSET_DETAIL}/{assetId}") { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            AssetDetailScreen(
                assetId = assetId,
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(AppDestination.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(AppDestination.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
