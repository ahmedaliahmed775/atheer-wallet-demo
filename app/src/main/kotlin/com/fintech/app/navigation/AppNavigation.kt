package com.fintech.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fintech.app.ui.screens.*
import com.fintech.app.viewmodel.FinTechViewModel

sealed class Screen(val route: String) {
    object Splash        : Screen("splash")
    object Login         : Screen("login")
    object Signup        : Screen("signup")
    object CustomerHome  : Screen("customer_home")
    object MerchantHome  : Screen("merchant_home")
    object Transfer      : Screen("transfer")
    object Voucher       : Screen("voucher")
    object History       : Screen("history")
    object Settings      : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val vm: FinTechViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isLoggedIn = uiState.isLoggedIn,
                userRole   = uiState.userRole,
                onNavigate = { role ->
                    val dest = if (role == "merchant") Screen.MerchantHome.route
                               else Screen.CustomerHome.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onLoginRequired = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                uiState   = uiState,
                onLogin   = { phone, pass -> vm.login(phone, pass) },
                onGoSignup = { navController.navigate(Screen.Signup.route) },
                onSuccess  = { role ->
                    val dest = if (role == "merchant") Screen.MerchantHome.route
                               else Screen.CustomerHome.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onClearError = { vm.clearError() }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                uiState      = uiState,
                onSignup     = { name, phone, pass, confirm, role -> vm.signup(name, phone, pass, confirm, role) },
                onGoLogin    = { navController.popBackStack() },
                onSuccess    = { role ->
                    val dest = if (role == "merchant") Screen.MerchantHome.route
                               else Screen.CustomerHome.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onClearError = { vm.clearError() }
            )
        }

        composable(Screen.CustomerHome.route) {
            CustomerHomeScreen(
                uiState        = uiState,
                onRefresh      = { vm.refreshBalance() },
                onTransfer     = { navController.navigate(Screen.Transfer.route) },
                onVoucher      = { navController.navigate(Screen.Voucher.route) },
                onHistory      = { navController.navigate(Screen.History.route) },
                onSettings     = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.MerchantHome.route) {
            MerchantHomeScreen(
                uiState    = uiState,
                onCashout  = { wallet, pass, voucher -> vm.cashout(wallet, pass, voucher) },
                onHistory  = { navController.navigate(Screen.History.route) },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onClearError = { vm.clearError() },
                onClearSuccess = { vm.clearSuccess() }
            )
        }

        composable(Screen.Transfer.route) {
            TransferScreen(
                uiState        = uiState,
                onTransfer     = { phone, amount, note -> vm.transfer(phone, amount, note) },
                onBack         = { navController.popBackStack() },
                onClearError   = { vm.clearError() },
                onClearSuccess = { vm.clearSuccess() }
            )
        }

        composable(Screen.Voucher.route) {
            VoucherScreen(
                uiState        = uiState,
                onGenerate     = { amount -> vm.generateVoucher(amount) },
                onBack         = { navController.popBackStack() },
                onClearError   = { vm.clearError() },
                onClearSuccess = { vm.clearSuccess() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                uiState   = uiState,
                onLoadMore = { page -> vm.loadTransactions(page) },
                onBack     = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                uiState  = uiState,
                onLogout = {
                    vm.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack   = { navController.popBackStack() }
            )
        }
    }
}
