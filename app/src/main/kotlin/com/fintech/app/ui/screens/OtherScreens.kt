package com.fintech.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintech.app.model.TransactionType
import com.fintech.app.ui.theme.*
import com.fintech.app.utils.formatAmount
import com.fintech.app.viewmodel.FinTechViewModel

// ─── History Screen ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: FinTechViewModel, onBackClick: () -> Unit) {
    val state by viewModel.appState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سجل العمليات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (state.transactions.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inbox, null, modifier = Modifier.size(64.dp), tint = Gray400)
                    Spacer(Modifier.height(16.dp))
                    Text("لا توجد عمليات", style = MaterialTheme.typography.titleMedium, color = Gray400)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(Gray50).padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.transactions) { txn ->
                    TransactionItem(txn = txn)
                }
            }
        }
    }
}

// ─── Sales History (Merchant) ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHistoryScreen(viewModel: FinTechViewModel, onBackClick: () -> Unit) {
    val state by viewModel.appState.collectAsStateWithLifecycle()
    val sales = state.transactions.filter { it.type == TransactionType.QR_PAYMENT }
    val totalSales = sales.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سجل المبيعات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Gray50).padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrightGreen.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("إجمالي المبيعات", color = Gray600, style = MaterialTheme.typography.bodyMedium)
                            Text("${totalSales.formatAmount()} ريال", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = BrightGreen)
                        }
                        Text("${sales.size} عملية", color = Gray600)
                    }
                }
            }

            if (sales.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inbox, null, modifier = Modifier.size(48.dp), tint = Gray400)
                            Text("لا توجد مبيعات بعد", color = Gray400)
                        }
                    }
                }
            } else {
                items(sales) { txn -> TransactionItem(txn = txn) }
            }
        }
    }
}

// ─── Settings Screen ──────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: FinTechViewModel,
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.appState.collectAsStateWithLifecycle()
    val user = state.currentUser ?: return
    var biometricEnabled by remember { mutableStateOf(user.biometricEnabled) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("تسجيل الخروج") },
            text = { Text("هل أنت متأكد من تسجيل الخروج؟") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("نعم", color = Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("إلغاء") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray50)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(RoyalBlue.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = RoyalBlue, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(user.phone, style = MaterialTheme.typography.bodyMedium, color = Gray600)
                        Text(
                            text = if (user.role == com.fintech.app.model.UserRole.CUSTOMER) "عميل" else "تاجر",
                            style = MaterialTheme.typography.bodySmall,
                            color = BrightGreen
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Settings options
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    // Biometric toggle
                    ListItem(
                        headlineContent = { Text("المصادقة البيومترية") },
                        supportingContent = { Text("استخدام البصمة للتأكيد", color = Gray600) },
                        leadingContent = { Icon(Icons.Default.Fingerprint, null, tint = RoyalBlue) },
                        trailingContent = {
                            Switch(
                                checked = biometricEnabled,
                                onCheckedChange = {
                                    biometricEnabled = it
                                    viewModel.toggleBiometric(it)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = RoyalBlue, checkedTrackColor = RoyalBlue.copy(alpha = 0.4f))
                            )
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    // Balance
                    ListItem(
                        headlineContent = { Text("الرصيد الحالي") },
                        supportingContent = { Text("${user.balance.formatAmount()} ريال", color = BrightGreen, fontWeight = FontWeight.Bold) },
                        leadingContent = { Icon(Icons.Default.AccountBalanceWallet, null, tint = BrightGreen) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    // QR
                    ListItem(
                        headlineContent = { Text("رمز QR الخاص بي") },
                        supportingContent = { Text("ID: ${user.id.take(8)}...", color = Gray600) },
                        leadingContent = { Icon(Icons.Default.QrCode, null, tint = RoyalBlue) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null, tint = Gray400) },
                        modifier = Modifier.clickable { }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Logout
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorLight)
            ) {
                Icon(Icons.Default.Logout, null, tint = Error)
                Spacer(Modifier.width(8.dp))
                Text("تسجيل الخروج", color = Error, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
