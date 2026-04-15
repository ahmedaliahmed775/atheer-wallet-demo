import os

directory = r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\ui\screens'

previews = {
    'BillPaymentScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun BillPaymentScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        BillPaymentScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onPayBill = { _, _, _, _ -> },
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
""",
    'CashInScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun CashInScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        CashInScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onCashIn = { _ -> },
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
""",
    'CashOutScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun CashOutScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        CashOutScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onGenerateCashout = { _ -> },
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
""",
    'ExternalTransferScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun ExternalTransferScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        ExternalTransferScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onTransfer = { _, _, _, _ -> },
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
""",
    'HistoryScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun HistoryScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        HistoryScreen(
            uiState = com.fintech.app.model.AppUiState(),
            onLoadMore = { _ -> },
            onBack = {},
            onTransactionClick = { _ -> }
        )
    }
}
""",
    'HomeScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun HomeScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        HomeScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onRefresh = {},
            onTransfer = {},
            onQrPay = {},
            onBillPayment = {},
            onCashOut = {},
            onCashIn = {},
            onExternalTransfer = {},
            onVoucher = {},
            onQrDisplay = {},
            onReceivePayment = {},
            onHistory = {},
            onSettings = {},
            onTransactionClick = { _ -> }
        )
    }
}
""",
    'MerchantHomeScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun MerchantHomeScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        MerchantHomeScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onCashout = { _, _, _ -> },
            onHistory = {},
            onSettings = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
""",
    'QrDisplayScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun QrDisplayScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        QrDisplayScreen(
            uiState = com.fintech.app.model.AppUiState(),
            onBack = {}
        )
    }
}
""",
    'QrPayScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun QrPayScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        QrPayScreen(
            uiState = com.fintech.app.model.AppUiState(balance = 150000.0),
            onQrPay = { _, _, _ -> },
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
""",
    'SplashScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun SplashScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        SplashScreen(
            isLoggedIn = true,
            userRole = "CUSTOMER",
            onNavigate = { _ -> },
            onLoginRequired = {}
        )
    }
}
""",
    'TransactionDetailScreen.kt': """
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun TransactionDetailScreenPreview() {
    com.fintech.app.ui.theme.FinTechTheme {
        TransactionDetailScreen(
            uiState = com.fintech.app.model.AppUiState(),
            transactionId = "TX-123456",
            onLoadDetail = { _ -> },
            onBack = {}
        )
    }
}
"""
}

for filename, preview_code in previews.items():
    path = os.path.join(directory, filename)
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    if 'fun ' + filename.replace('.kt', '') + 'Preview(' not in content:
        with open(path, 'a', encoding='utf-8') as f:
            f.write('\\n' + preview_code.strip() + '\\n')
            print(f"Added preview to {filename}")
    else:
        print(f"Preview already exists in {filename}")

