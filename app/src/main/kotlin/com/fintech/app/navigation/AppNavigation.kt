package com.fintech.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fintech.app.ui.screens.*
import com.fintech.app.viewmodel.FinTechViewModel

sealed class Screen(val route: String) {
    object Splash            : Screen("splash")
    object Login             : Screen("login")
    object Signup            : Screen("signup")
    object Home              : Screen("home")
    object Transfer          : Screen("transfer")
    object ExternalTransfer  : Screen("external_transfer")
    object Voucher           : Screen("voucher")
    object BillPayment       : Screen("bill_payment")
    object QrPay             : Screen("qr_pay")
    object QrDisplay         : Screen("qr_display")
    object CashOut           : Screen("cash_out")
    object CashIn            : Screen("cash_in")
    object ReceivePayment    : Screen("receive_payment")
    object History           : Screen("history")
    object Settings          : Screen("settings")
    object TransactionDetail : Screen("transaction_detail/{txnId}") {
        fun withId(id: String) = "transaction_detail/$id"
    }
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
                onNavigate = {
                    navController.navigate(Screen.Home.route) {
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
                uiState    = uiState,
                onLogin    = { phone, pass -> vm.login(phone, pass) },
                onGoSignup = { navController.navigate(Screen.Signup.route) },
                onSuccess  = {
                    navController.navigate(Screen.Home.route) {
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
                onSuccess    = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onClearError = { vm.clearError() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                uiState            = uiState,
                onRefresh          = { vm.refreshBalance(); vm.loadTransactions() },
                onTransfer         = { navController.navigate(Screen.Transfer.route) },
                onQrPay            = { navController.navigate(Screen.QrPay.route) },
                onBillPayment      = { navController.navigate(Screen.BillPayment.route) },
                onCashOut          = { navController.navigate(Screen.CashOut.route) },
                onCashIn           = { navController.navigate(Screen.CashIn.route) },
                onExternalTransfer = { navController.navigate(Screen.ExternalTransfer.route) },
                onVoucher          = { navController.navigate(Screen.Voucher.route) },
                onQrDisplay        = { navController.navigate(Screen.QrDisplay.route) },
                onReceivePayment   = { navController.navigate(Screen.ReceivePayment.route) },
                onHistory          = { navController.navigate(Screen.History.route) },
                onSettings         = { navController.navigate(Screen.Settings.route) },
                onTransactionClick = { id -> navController.navigate(Screen.TransactionDetail.withId(id)) }
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

        composable(Screen.ExternalTransfer.route) {
            ExternalTransferScreen(
                uiState        = uiState,
                onTransfer     = { phone, name, amount, note -> vm.transferExternal(phone, name, amount, note) },
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

        composable(Screen.BillPayment.route) {
            BillPaymentScreen(
                uiState        = uiState,
                onPayBill      = { cat, prov, acc, amt -> vm.payBill(cat, prov, acc, amt) },
                onBack         = { navController.popBackStack() },
                onClearError   = { vm.clearError() },
                onClearSuccess = { vm.clearSuccess() }
            )
        }

        composable(Screen.QrPay.route) {
            QrPayScreen(
                uiState        = uiState,
                onQrPay        = { phone, amount, note -> vm.qrPay(phone, amount, note) },
                onBack         = { navController.popBackStack() },
                onClearError   = { vm.clearError() },
                onClearSuccess = { vm.clearSuccess() }
            )
        }

        composable(Screen.QrDisplay.route) {
            QrDisplayScreen(
                uiState = uiState,
                onBack  = { navController.popBackStack() }
            )
        }

        composable(Screen.CashOut.route) {
            CashOutScreen(
                uiState           = uiState,
                onGenerateCashout = { amount -> vm.generateCashout(amount) },
                onBack            = { navController.popBackStack() },
                onClearError      = { vm.clearError() },
                onClearSuccess    = { vm.clearSuccess() }
            )
        }

        composable(Screen.CashIn.route) {
            CashInScreen(
                uiState        = uiState,
                onCashIn       = { amount -> vm.cashIn(amount) },
                onBack         = { navController.popBackStack() },
                onClearError   = { vm.clearError() },
                onClearSuccess = { vm.clearSuccess() }
            )
        }

        composable(Screen.ReceivePayment.route) {
            MerchantHomeScreen(
                uiState        = uiState,
                onCashout      = { wallet, pass, voucher -> vm.cashout(wallet, pass, voucher) },
                onHistory      = { navController.navigate(Screen.History.route) },
                onSettings     = { navController.navigate(Screen.Settings.route) },
                onClearError   = { vm.clearError() },
                onClearSuccess = { vm.clearSuccess() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                uiState            = uiState,
                onLoadMore         = { page -> vm.loadTransactions(page) },
                onBack             = { navController.popBackStack() },
                onTransactionClick = { id -> navController.navigate(Screen.TransactionDetail.withId(id)) }
            )
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("txnId") { type = NavType.StringType })
        ) { backStackEntry ->
            val txnId = backStackEntry.arguments?.getString("txnId") ?: ""
            TransactionDetailScreen(
                uiState       = uiState,
                transactionId = txnId,
                onLoadDetail  = { vm.loadTransactionDetail(it) },
                onBack        = { navController.popBackStack() }
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
