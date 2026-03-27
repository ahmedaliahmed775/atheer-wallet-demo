package com.fintech.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintech.app.ui.components.*
import com.fintech.app.ui.theme.*
import com.fintech.app.utils.formatAmount
import com.fintech.app.viewmodel.FinTechViewModel
import kotlinx.coroutines.launch

@Composable
fun MerchantHomeScreen(
    viewModel: FinTechViewModel,
    onSalesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.appState.collectAsStateWithLifecycle()
    val user = state.currentUser ?: return
    val haptic = LocalHapticFeedback.current

    var enteredAmount by remember { mutableStateOf("") }
    var showTransferModal by remember { mutableStateOf(false) }
    var showBillModal by remember { mutableStateOf(false) }
    var showSuccessScreen by remember { mutableStateOf(false) }
    var successInfo by remember { mutableStateOf<Triple<String, Double, String>?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val totalSales = state.transactions
        .filter { it.type == com.fintech.app.model.TransactionType.QR_PAYMENT }
        .sumOf { it.amount }

    if (showSuccessScreen && successInfo != null) {
        SuccessScreen(
            title = successInfo!!.first,
            amount = successInfo!!.second,
            recipient = successInfo!!.third,
            referenceNumber = "TXN-${System.currentTimeMillis()}",
            onDone = { showSuccessScreen = false; successInfo = null; enteredAmount = "" }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.PointOfSale, null) },
                    label = { Text("المحطة") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { onSalesClick() },
                    icon = { Icon(Icons.Default.BarChart, null) },
                    label = { Text("المبيعات") }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray50)
                .padding(paddingValues)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(DarkBlue, RoyalBlue)))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(user.storeName ?: user.name, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("إجمالي المبيعات: ${totalSales.formatAmount()} ريال", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, null, tint = Color.White)
                    }
                }
            }

            // Amount display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("المبلغ", color = Gray600, style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (enteredAmount.isEmpty()) "0.00" else enteredAmount,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (enteredAmount.isEmpty()) Gray400 else DarkBlue
                    )
                    Text("ريال سعودي", color = Gray400, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Numpad
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    val rows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf(".", "0", "C")
                    )
                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                NumpadButton(
                                    key = key,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        when (key) {
                                            "C" -> enteredAmount = ""
                                            "." -> if (!enteredAmount.contains(".")) enteredAmount += "."
                                            else -> {
                                                if (enteredAmount.length < 8) enteredAmount += key
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Action buttons
            val hasAmount = enteredAmount.isNotEmpty() && enteredAmount.toDoubleOrNull() != null && enteredAmount.toDouble() > 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Collect (QR)
                Button(
                    onClick = {
                        val amount = enteredAmount.toDoubleOrNull() ?: return@Button
                        viewModel.collectPayment(amount)
                        successInfo = Triple("تم التحصيل!", amount, "تحصيل QR")
                        showSuccessScreen = true
                    },
                    enabled = hasAmount,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrightGreen)
                ) {
                    Icon(Icons.Default.QrCode, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("تحصيل", fontWeight = FontWeight.Bold)
                }

                // Transfer
                OutlinedButton(
                    onClick = { showTransferModal = true },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, RoyalBlue)
                ) {
                    Icon(Icons.Default.SwapHoriz, null, tint = RoyalBlue, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("حوالة", color = RoyalBlue, fontWeight = FontWeight.Bold)
                }

                // Bills
                OutlinedButton(
                    onClick = { showBillModal = true },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, ElectricityColor)
                ) {
                    Icon(Icons.Default.Receipt, null, tint = ElectricityColor, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("فواتير", color = ElectricityColor, fontWeight = FontWeight.Bold)
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
                successInfo = Triple("تم التحويل!", amount, recipient)
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
                successInfo = Triple("تم السداد!", amount, service.labelAr)
                showSuccessScreen = true
            },
            onDismiss = { showBillModal = false }
        )
    }
}

@Composable
private fun NumpadButton(key: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val isDelete = key == "C"
    val scale = remember { androidx.compose.animation.core.Animatable(1f) }
    val scope = rememberCoroutineScope()

    OutlinedButton(
        onClick = {
            scope.launch {
                scale.animateTo(0.9f, tween(80))
                scale.animateTo(1f, tween(80))
            }
            onClick()
        },
        modifier = modifier
            .height(56.dp)
            .scale(scale.value),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isDelete) ErrorLight else Color.White
        ),
        border = BorderStroke(1.dp, if (isDelete) Error else Gray200)
    ) {
        if (isDelete) {
            Icon(Icons.Default.Backspace, null, tint = Error)
        } else {
            Text(
                key,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = DarkBlue
            )
        }
    }
}
