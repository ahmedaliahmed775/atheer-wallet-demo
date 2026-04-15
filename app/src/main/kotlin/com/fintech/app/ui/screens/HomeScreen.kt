package com.fintech.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: AppUiState,
    onRefresh: () -> Unit,
    onTransfer: () -> Unit,
    onQrPay: () -> Unit,
    onBillPayment: () -> Unit,
    onCashOut: () -> Unit,
    onCashIn: () -> Unit,
    onExternalTransfer: () -> Unit,
    onVoucher: () -> Unit,
    onQrDisplay: () -> Unit,
    onReceivePayment: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    onTransactionClick: (String) -> Unit
) {
    val isMerchant = uiState.userRole == "merchant"

    LaunchedEffect(Unit) { onRefresh() }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("الرئيسية") },
                    selected = true,
                    onClick = {},
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = if (isMerchant) Merchant else Primary,
                        indicatorColor = if (isMerchant) Merchant.copy(.12f) else Primary.copy(.12f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Receipt, null) },
                    label = { Text("السجل") },
                    selected = false,
                    onClick = onHistory
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("الإعدادات") },
                    selected = false,
                    onClick = onSettings
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ─── Balance Header ─────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isMerchant) listOf(Merchant, Color(0xFF3D35A0))
                                     else listOf(Primary, PrimaryDk)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "مرحباً، ${uiState.userName}",
                                color = Color.White.copy(.85f),
                                fontSize = 14.sp
                            )
                            if (isMerchant) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White.copy(.2f),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        "حساب تاجر",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        IconButton(onRefresh) {
                            Icon(Icons.Default.Refresh, null, tint = Color.White)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "رصيدك الحالي",
                        color = Color.White.copy(.7f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "%,.0f ﷼".format(uiState.balance),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        uiState.userPhone,
                        color = Color.White.copy(.6f),
                        fontSize = 13.sp
                    )
                }
            }

            // ─── Services Grid ──────────────────────────────
            Column(Modifier.padding(20.dp)) {
                Text(
                    "خدماتك",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // Row 1: common services
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ServiceItem(Icons.Default.Send, "تحويل", Primary, Modifier.weight(1f), onTransfer)
                    ServiceItem(Icons.Default.QrCodeScanner, "دفع QR", Color(0xFF534AB7), Modifier.weight(1f), onQrPay)
                    ServiceItem(Icons.Default.Receipt, "فواتير", Color(0xFFF59E0B), Modifier.weight(1f), onBillPayment)
                }

                Spacer(Modifier.height(10.dp))

                // Row 2
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ServiceItem(Icons.Default.MoneyOff, "سحب نقدي", Color(0xFFEF4444), Modifier.weight(1f), onCashOut)
                    ServiceItem(Icons.Default.AccountBalanceWallet, "إيداع", Color(0xFF22C55E), Modifier.weight(1f), onCashIn)
                    ServiceItem(Icons.Default.SendAndArchive, "حوالة خارجية", Color(0xFF06B6D4), Modifier.weight(1f), onExternalTransfer)
                }

                // Merchant-only services
                if (isMerchant) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ServiceItem(Icons.Default.QrCode2, "QR التاجر", Merchant, Modifier.weight(1f), onQrDisplay)
                        ServiceItem(Icons.Default.PointOfSale, "استلام دفعة", Color(0xFF8B5CF6), Modifier.weight(1f), onReceivePayment)
                        ServiceItem(Icons.Default.CardGiftcard, "قسيمة", Primary, Modifier.weight(1f), onVoucher)
                    }
                } else {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ServiceItem(Icons.Default.CardGiftcard, "قسيمة دفع", Color(0xFF534AB7), Modifier.weight(1f), onVoucher)
                        Spacer(Modifier.weight(1f))
                        Spacer(Modifier.weight(1f))
                    }
                }

                // ─── Recent Transactions ────────────────────
                Spacer(Modifier.height(24.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("آخر المعاملات", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    TextButton(onClick = onHistory) {
                        Text("عرض الكل", color = if (isMerchant) Merchant else Primary, fontSize = 13.sp)
                    }
                }

                if (uiState.transactions.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ReceiptLong,
                                null,
                                tint = TextMuted.copy(.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("لا توجد معاملات بعد", color = TextMuted)
                        }
                    }
                } else {
                    uiState.transactions.take(5).forEach { txn ->
                        TransactionRowClickable(txn) { onTransactionClick(txn.id) }
                        HorizontalDivider(color = Color(0xFFF1EFE8))
                    }
                }

                // ─── Merchant Stats ─────────────────────────
                if (isMerchant && uiState.transactions.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    Text("إحصائيات", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            "المبيعات",
                            "%,.0f ﷼".format(uiState.transactions.filter { it.type == "CREDIT" }.sumOf { it.amount }),
                            Merchant,
                            Modifier.weight(1f)
                        )
                        StatCard(
                            "العمليات",
                            uiState.transactions.size.toString(),
                            Primary,
                            Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceItem(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(.12f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        }
    }
}


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
