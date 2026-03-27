package com.fintech.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintech.app.model.*
import com.fintech.app.ui.components.*
import com.fintech.app.ui.theme.*
import com.fintech.app.utils.formatAmount
import com.fintech.app.viewmodel.FinTechViewModel
import java.time.format.DateTimeFormatter

@Composable
fun CustomerHomeScreen(
    viewModel: FinTechViewModel,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.appState.collectAsStateWithLifecycle()
    val user = state.currentUser ?: return

    var showTransferModal by remember { mutableStateOf(false) }
    var showBillModal by remember { mutableStateOf(false) }
    var showSuccessScreen by remember { mutableStateOf(false) }
    var successInfo by remember { mutableStateOf<Triple<String, Double, String>?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

    if (showSuccessScreen && successInfo != null) {
        SuccessScreen(
            title = successInfo!!.first,
            amount = successInfo!!.second,
            recipient = successInfo!!.third,
            referenceNumber = "TXN-${System.currentTimeMillis()}",
            onDone = { showSuccessScreen = false; successInfo = null }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("الرئيسية") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { onHistoryClick() },
                    icon = { Icon(Icons.Default.History, null) },
                    label = { Text("السجل") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { onSettingsClick() },
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("الإعدادات") }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray50)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(GradientStart, GradientEnd))
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "مرحباً، ${user.name.split(" ").first()} 👋",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = "رصيدك الحالي",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            IconButton(onClick = onLogout) {
                                Icon(Icons.Default.Logout, null, tint = Color.White)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "${user.balance.formatAmount()} ريال",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            item {
                // Services Grid
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-16).dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("الخدمات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkBlue)
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ServiceButton(
                                icon = Icons.Default.SwapHoriz,
                                label = "حوالة",
                                color = RoyalBlue,
                                onClick = { showTransferModal = true }
                            )
                            ServiceButton(
                                icon = Icons.Default.Receipt,
                                label = "فواتير",
                                color = ElectricityColor,
                                onClick = { showBillModal = true }
                            )
                            ServiceButton(
                                icon = Icons.Default.QrCode,
                                label = "QR",
                                color = BrightGreen,
                                onClick = { /* Show QR */ }
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(4.dp)) }

            item {
                // Recent transactions header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("آخر العمليات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkBlue)
                    TextButton(onClick = onHistoryClick) {
                        Text("عرض الكل", color = RoyalBlue)
                    }
                }
            }

            val recentTxns = state.transactions.take(5)
            if (recentTxns.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inbox, null, modifier = Modifier.size(48.dp), tint = Gray400)
                            Text("لا توجد عمليات بعد", color = Gray400, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                items(recentTxns) { txn ->
                    TransactionItem(txn = txn, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }
            }
        }
    }

    if (showTransferModal) {
        TransferModal(
            currentBalance = user.balance,
            onConfirm = { recipient, amount ->
                showTransferModal = false
                viewModel.transferMoney(recipient, amount)
                successInfo = Triple("تم التحويل بنجاح!", amount, recipient)
                showSuccessScreen = true
            },
            onDismiss = { showTransferModal = false }
        )
    }

    if (showBillModal) {
        BillPaymentModal(
            currentBalance = user.balance,
            onConfirm = { service, amount ->
                showBillModal = false
                viewModel.payBill(service, amount)
                successInfo = Triple("تم السداد بنجاح!", amount, service.labelAr)
                showSuccessScreen = true
            },
            onDismiss = { showBillModal = false }
        )
    }
}

@Composable
fun ServiceButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(60.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = color.copy(alpha = 0.12f))
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = Gray600)
    }
}

@Composable
fun TransactionItem(txn: Transaction, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (icon, color) = when (txn.type) {
                TransactionType.TRANSFER -> Icons.Default.ArrowUpward to Error
                TransactionType.RECEIVED -> Icons.Default.ArrowDownward to Success
                TransactionType.BILL_PAYMENT -> Icons.Default.Receipt to Warning
                TransactionType.QR_PAYMENT -> Icons.Default.QrCode to RoyalBlue
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = txn.recipientName.ifEmpty { txn.recipient },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = txn.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400
                )
            }

            val amountColor = when (txn.type) {
                TransactionType.RECEIVED -> Success
                else -> Error
            }
            val amountPrefix = if (txn.type == TransactionType.RECEIVED) "+" else "-"

            Text(
                text = "$amountPrefix${txn.amount.formatAmount()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}
