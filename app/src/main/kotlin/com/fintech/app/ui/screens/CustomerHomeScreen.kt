package com.fintech.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.model.TransactionDto
import com.fintech.app.ui.theme.FinTechTheme
import com.fintech.app.ui.theme.Primary
import com.fintech.app.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    uiState: AppUiState,
    onRefresh: () -> Unit,
    onTransfer: () -> Unit,
    onQrPay: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    LaunchedEffect(Unit) { onRefresh() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("الرئيسية") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.History, null) }, label = { Text("السجل") }, selected = false, onClick = onHistory)
                NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("الإعدادات") }, selected = false, onClick = onSettings)
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            // Header
            Surface(color = Primary) {
                Column(Modifier.fillMaxWidth().padding(24.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("مرحباً، ${uiState.userName}", color = Color.White.copy(.8f), fontSize = 14.sp)
                            Text("رصيدك الحالي", color = Color.White.copy(.7f), fontSize = 12.sp)
                        }
                        IconButton(onRefresh) { Icon(Icons.Default.Refresh, null, tint = Color.White) }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "%.0f ﷼".format(uiState.balance),
                        color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold
                    )
                    Text(uiState.userPhone, color = Color.White.copy(.7f), fontSize = 13.sp)
                }
            }

            // Quick Actions
            Column(Modifier.padding(20.dp)) {
                Text("خدماتك السريعة", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickAction(icon = Icons.Default.Send, label = "تحويل لعميل", color = Primary, modifier = Modifier.weight(1f), onClick = onTransfer)
                    QuickAction(icon = Icons.Default.QrCode2, label = "دفع مشتريات", color = Color(0xFF534AB7), modifier = Modifier.weight(1f), onClick = onQrPay)
                }

                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("آخر المعاملات", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    TextButton(onClick = onHistory) { Text("عرض الكل", color = Primary, fontSize = 13.sp) }
                }

                if (uiState.transactions.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("لا توجد معاملات بعد", color = TextMuted)
                    }
                } else {
                    uiState.transactions.take(5).forEach { txn ->
                        TransactionRow(txn)
                        HorizontalDivider(color = Color(0xFFF1EFE8))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerHomeScreenPreview() {
    FinTechTheme {
        CustomerHomeScreen(
            uiState = AppUiState(
                userName = "أحمد محمد",
                balance = 50000.0,
                userPhone = "777123456",
                transactions = listOf(
                    TransactionDto(
                        id = "1",
                        refId = "TXN123",
                        type = "CREDIT",
                        txnType = "TRANSFER",
                        amount = 1500.0,
                        counterparty = "محمد علي",
                        counterPhone = "770111222",
                        note = "تحويل",
                        status = "completed",
                        timestamp = "2023-10-27T10:00:00"
                    ),
                    TransactionDto(
                        id = "2",
                        refId = "TXN124",
                        type = "DEBIT",
                        txnType = "CASHOUT",
                        amount = 2000.0,
                        counterparty = "سوبر ماركت السعيد",
                        counterPhone = "771222333",
                        note = "مشتريات",
                        status = "completed",
                        timestamp = "2023-10-26T15:30:00"
                    )
                )
            ),
            onRefresh = {},
            onTransfer = {},
            onQrPay = {},
            onHistory = {},
            onSettings = {}
        )
    }
}
