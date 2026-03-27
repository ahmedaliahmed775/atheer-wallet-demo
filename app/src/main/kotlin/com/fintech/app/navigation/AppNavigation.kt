package com.fintech.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fintech.app.model.UserRole
import com.fintech.app.ui.screens.*
import com.fintech.app.viewmodel.FinTechViewModel

sealed class Screen(val route: String) {
    object Splash        : Screen("splash")
    object SignupRole    : Screen("signup_role")
    object SignupData    : Screen("signup_data/{role}") {
        fun createRoute(role: String) = "signup_data/$role"
    }
    object CustomerHome  : Screen("customer_home")
    object MerchantHome  : Screen("merchant_home")
    object History       : Screen("history")
    object Settings      : Screen("settings")
    object SalesHistory  : Screen("sales_history")
}

@Composable
fun AppNavigation(viewModel: FinTechViewModel) {
    val navController = rememberNavController()
    val state by viewModel.appState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { it / 4 }
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { -it / 4 }
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { -it / 4 }
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { it / 4 }
            )
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                viewModel = viewModel,
                onLoginSuccess = { user ->
                    if (user.role == UserRole.CUSTOMER)
                        navController.navigate(Screen.CustomerHome.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    else
                        navController.navigate(Screen.MerchantHome.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                },
                onSignupClick = {
                    navController.navigate(Screen.SignupRole.route)
                }
            )
        }

        composable(Screen.SignupRole.route) {
            SignupRoleScreen(
                onRoleSelected = { role ->
                    navController.navigate(Screen.SignupData.createRoute(role.name))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.SignupData.route) { backStackEntry ->
            val roleStr = backStackEntry.arguments?.getString("role") ?: "CUSTOMER"
            val role = UserRole.valueOf(roleStr)
            SignupDataScreen(
                role = role,
                viewModel = viewModel,
                onSuccess = {
                    val dest = if (role == UserRole.CUSTOMER) Screen.CustomerHome.route
                               else Screen.MerchantHome.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CustomerHome.route) {
            CustomerHomeScreen(
                viewModel = viewModel,
                onHistoryClick = { navController.navigate(Screen.History.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MerchantHome.route) {
            MerchantHomeScreen(
                viewModel = viewModel,
                onSalesClick = { navController.navigate(Screen.SalesHistory.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.SalesHistory.route) {
            SalesHistoryScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
