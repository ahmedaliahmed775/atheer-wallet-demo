package com.fintech.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.fintech.app.model.*
import kotlinx.coroutines.delay

// ─── Shared Colors ────────────────────────────────────────
private val Primary   = Color(0xFF1D9E75)
private val PrimaryDk = Color(0xFF0F6E56)
private val Merchant  = Color(0xFF534AB7)
private val BgCard    = Color(0xFFF8F7F4)
private val TextMuted = Color(0xFF888780)

// ─── Splash Screen ────────────────────────────────────────
@Composable
fun SplashScreen(
    isLoggedIn: Boolean,
    userRole: String,
    onNavigate: (String) -> Unit,
    onLoginRequired: () -> Unit
) {
    LaunchedEffect(isLoggedIn) {
        delay(1200)
        if (isLoggedIn) onNavigate(userRole) else onLoginRequired()
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Primary,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("FinTech Pay", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("محفظتك الرقمية", fontSize = 14.sp, color = TextMuted)
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = Primary, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
        }
    }
}

// ─── Login Screen ─────────────────────────────────────────
@Composable
fun LoginScreen(
    uiState: AppUiState,
    onLogin: (String, String) -> Unit,
    onGoSignup: () -> Unit,
    onSuccess: (String) -> Unit,
    onClearError: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onSuccess(uiState.userRole)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))
        Surface(shape = RoundedCornerShape(16.dp), color = Primary, modifier = Modifier.size(64.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("تسجيل الدخول", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("أدخل بيانات حسابك للمتابعة", fontSize = 14.sp, color = TextMuted, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(36.dp))

        OutlinedTextField(
            value = phone, onValueChange = { phone = it; onClearError() },
            label = { Text("رقم الهاتف") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it; onClearError() },
            label = { Text("كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton({ passVisible = !passVisible }) {
                    Icon(if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )

        uiState.error?.let { err ->
            Spacer(Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB)), modifier = Modifier.fillMaxWidth()) {
                Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onLogin(phone, password) },
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text("دخول", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onGoSignup) {
            Text("ليس لديك حساب؟ ", color = TextMuted)
            Text("إنشاء حساب", color = Primary, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── Signup Screen ────────────────────────────────────────
@Composable
fun SignupScreen(
    uiState: AppUiState,
    onSignup: (String, String, String, String, String) -> Unit,
    onGoLogin: () -> Unit,
    onSuccess: (String) -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("customer") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onSuccess(uiState.userRole)
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Text("إنشاء حساب جديد", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("أدخل بياناتك للتسجيل", fontSize = 14.sp, color = TextMuted, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم الكامل") },
            leadingIcon = { Icon(Icons.Default.Person, null) }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("رقم الهاتف") },
            leadingIcon = { Icon(Icons.Default.Phone, null) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = PasswordVisualTransformation(),
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = confirm, onValueChange = { confirm = it }, label = { Text("تأكيد كلمة المرور") },
            leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = PasswordVisualTransformation(),
            singleLine = true, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("customer" to "عميل", "merchant" to "تاجر").forEach { (v, label) ->
                val selected = role == v
                OutlinedButton(
                    onClick = { role = v },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected) Primary else Color.Transparent,
                        contentColor   = if (selected) Color.White else TextMuted
                    ),
                    border = BorderStroke(1.dp, if (selected) Primary else Color(0xFFD3D1C7)),
                    modifier = Modifier.weight(1f)
                ) { Text(label) }
            }
        }

        uiState.error?.let { err ->
            Spacer(Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB)), modifier = Modifier.fillMaxWidth()) {
                Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onSignup(name, phone, password, confirm, role) },
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text("إنشاء الحساب", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onGoLogin) {
            Text("لديك حساب؟ ", color = TextMuted)
            Text("تسجيل الدخول", color = Primary, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── Customer Home Screen ─────────────────────────────────
@Composable
fun CustomerHomeScreen(
    uiState: AppUiState,
    onRefresh: () -> Unit,
    onTransfer: () -> Unit,
    onVoucher: () -> Unit,
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
                    QuickAction(icon = Icons.Default.SendMoney, label = "تحويل", color = Primary, modifier = Modifier.weight(1f), onClick = onTransfer)
                    QuickAction(icon = Icons.Default.QrCode2, label = "قسيمة دفع", color = Color(0xFF534AB7), modifier = Modifier.weight(1f), onClick = onVoucher)
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
                        Divider(color = Color(0xFFF1EFE8))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = color.copy(.12f), modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(24.dp)) }
            }
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun TransactionRow(txn: TransactionDto) {
    val isCredit = txn.type == "CREDIT"
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = CircleShape, color = if (isCredit) Color(0xFFE1F5EE) else Color(0xFFFCEBEB), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        null, tint = if (isCredit) PrimaryDk else Color(0xFFA32D2D), modifier = Modifier.size(18.dp))
                }
            }
            Column {
                Text(txn.counterparty ?: "معاملة", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text(txn.timestamp.take(10), fontSize = 11.sp, color = TextMuted)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${if (isCredit) "+" else "-"}%.0f ﷼".format(txn.amount),
                color = if (isCredit) PrimaryDk else Color(0xFFA32D2D),
                fontWeight = FontWeight.SemiBold, fontSize = 14.sp
            )
            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE1F5EE)) {
                Text("مكتمل", fontSize = 9.sp, color = PrimaryDk, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
    }
}

// ─── Merchant Home Screen ─────────────────────────────────
@Composable
fun MerchantHomeScreen(
    uiState: AppUiState,
    onCashout: (String, String, String) -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var voucherCode by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.lastCashout) {
        if (uiState.lastCashout != null) {
            showSuccess = true
            voucherCode = ""
            password    = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("لوحة التاجر") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Merchant, titleContentColor = Color.White),
                actions = {
                    IconButton(onSettings) { Icon(Icons.Default.Settings, null, tint = Color.White) }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("الرئيسية") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.History, null) }, label = { Text("السجل") }, selected = false, onClick = onHistory)
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {

            // Stats
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("مبيعات اليوم", "%.0f ﷼".format(uiState.transactions.filter { it.type == "CREDIT" }.sumOf { it.amount }), Merchant, Modifier.weight(1f))
                StatCard("العمليات", uiState.transactions.size.toString(), Primary, Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
            Text("استلام دفعة بقسيمة", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 12.dp))

            Card(colors = CardDefaults.cardColors(containerColor = BgCard), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = voucherCode,
                        onValueChange = { voucherCode = it.uppercase(); onClearError() },
                        label = { Text("رمز القسيمة") },
                        leadingIcon = { Icon(Icons.Default.QrCode2, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; onClearError() },
                        label = { Text("كلمة مرور التاجر") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    uiState.error?.let { err ->
                        Spacer(Modifier.height(8.dp))
                        Text(err, color = Color(0xFFA32D2D), fontSize = 12.sp)
                    }

                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = { onCashout(uiState.userPhone, password, voucherCode) },
                        enabled = !uiState.isLoading && voucherCode.length >= 6 && password.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Merchant),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        else Text("تأكيد الاستلام", fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Success dialog
            if (showSuccess && uiState.lastCashout != null) {
                Spacer(Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5EE)), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = PrimaryDk)
                            Spacer(Modifier.width(8.dp))
                            Text("تم الاستلام بنجاح", fontWeight = FontWeight.SemiBold, color = PrimaryDk)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("المبلغ: %.0f ﷼".format(uiState.lastCashout!!.amount), fontSize = 13.sp)
                        Text("المرجع: ${uiState.lastCashout!!.refId}", fontSize = 11.sp, color = TextMuted)
                        TextButton({ showSuccess = false; onClearSuccess() }) { Text("إغلاق") }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("آخر العمليات", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))
            uiState.transactions.take(5).forEach { txn ->
                TransactionRow(txn)
                Divider(color = Color(0xFFF1EFE8))
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(.1f)), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, color = color.copy(.8f))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

// ─── Transfer Screen ──────────────────────────────────────
@Composable
fun TransferScreen(
    uiState: AppUiState,
    onTransfer: (String, Double, String) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var phone  by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note   by remember { mutableStateOf("") }

    LaunchedEffect(uiState.lastTransfer) {
        if (uiState.lastTransfer != null) { phone = ""; amount = ""; note = "" }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("تحويل أموال") }, navigationIcon = {
                IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) }
            })
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {

            // Balance card
            Card(colors = CardDefaults.cardColors(containerColor = Primary), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("رصيدك المتاح", color = Color.White.copy(.8f), fontSize = 13.sp)
                    Text("%.0f ﷼".format(uiState.balance), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(value = phone, onValueChange = { phone = it; onClearError() },
                label = { Text("رقم هاتف المستلم") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = amount, onValueChange = { amount = it; onClearError() },
                label = { Text("المبلغ (ريال يمني)") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = note, onValueChange = { note = it },
                label = { Text("ملاحظة (اختياري)") },
                singleLine = true, modifier = Modifier.fillMaxWidth())

            uiState.error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB))) {
                    Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            uiState.lastTransfer?.let { t ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5EE))) {
                    Column(Modifier.padding(12.dp)) {
                        Text("تم التحويل بنجاح", color = PrimaryDk, fontWeight = FontWeight.Medium)
                        Text("إلى: ${t.receiverName} — %.0f ﷼".format(t.amount), fontSize = 13.sp)
                        Text("مرجع: ${t.refId}", fontSize = 11.sp, color = TextMuted)
                        TextButton({ onClearSuccess() }) { Text("إغلاق") }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onTransfer(phone, amount.toDoubleOrNull() ?: 0.0, note.ifBlank { "تحويل" }) },
                enabled = !uiState.isLoading && phone.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("تحويل الآن", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ─── Voucher Screen ───────────────────────────────────────
@Composable
fun VoucherScreen(
    uiState: AppUiState,
    onGenerate: (Double) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var amount by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("إنشاء قسيمة دفع") }, navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEDFE)), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("كيف تعمل القسيمة؟", fontWeight = FontWeight.SemiBold, color = Color(0xFF3C3489))
                    Spacer(Modifier.height(6.dp))
                    Text("1. أدخل المبلغ وأنشئ القسيمة", fontSize = 13.sp, color = Color(0xFF534AB7))
                    Text("2. أعطِ الرمز للتاجر ليُدخله في جهازه", fontSize = 13.sp, color = Color(0xFF534AB7))
                    Text("3. تُستهلك القسيمة تلقائياً ← صالحة 5 دقائق", fontSize = 13.sp, color = Color(0xFF534AB7))
                }
            }

            Spacer(Modifier.height(20.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Primary), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("رصيدك المتاح", color = Color.White.copy(.8f), fontSize = 13.sp)
                    Text("%.0f ﷼".format(uiState.balance), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = amount, onValueChange = { amount = it; onClearError() },
                label = { Text("مبلغ القسيمة (ريال يمني)") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            uiState.error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB))) {
                    Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            uiState.lastVoucher?.let { v ->
                Spacer(Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEDFE)), shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("رمز القسيمة", fontSize = 13.sp, color = Color(0xFF534AB7))
                        Spacer(Modifier.height(8.dp))
                        Text(v.voucherCode, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF26215C), letterSpacing = 4.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("المبلغ: %.0f ﷼".format(v.amount), fontSize = 14.sp, color = Color(0xFF534AB7))
                        Text("صالحة حتى: ${v.expiresAt.take(19).replace('T', ' ')}", fontSize = 11.sp, color = Color(0xFF888780))
                        Spacer(Modifier.height(8.dp))
                        TextButton({ onClearSuccess() }) { Text("إغلاق", color = Color(0xFF534AB7)) }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onGenerate(amount.toDoubleOrNull() ?: 0.0) },
                enabled = !uiState.isLoading && (amount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF534AB7)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("إنشاء القسيمة", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ─── History Screen ───────────────────────────────────────
@Composable
fun HistoryScreen(uiState: AppUiState, onLoadMore: (Int) -> Unit, onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(1) }
    val total = uiState.transactions.sumOf { if (it.type == "CREDIT") it.amount else 0.0 }

    Scaffold(
        topBar = { TopAppBar(title = { Text("سجل المعاملات") }, navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${uiState.transactions.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
                            Text("عملية", fontSize = 12.sp, color = TextMuted)
                        }
                        Divider(Modifier.height(36.dp).width(1.dp), color = Color(0xFFD3D1C7))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("%.0f ﷼".format(total), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
                            Text("إجمالي الاستلام", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            }
            items(uiState.transactions) { txn ->
                Column(Modifier.padding(horizontal = 16.dp)) {
                    TransactionRow(txn)
                    Divider(color = Color(0xFFF1EFE8))
                }
            }
            if (uiState.transactions.isNotEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        OutlinedButton(onClick = { currentPage++; onLoadMore(currentPage) }) {
                            Text("تحميل المزيد")
                        }
                    }
                }
            }
        }
    }
}

// ─── Settings Screen ──────────────────────────────────────
@Composable
fun SettingsScreen(uiState: AppUiState, onLogout: () -> Unit, onBack: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("تأكيد الخروج") },
            text = { Text("هل تريد تسجيل الخروج من حسابك؟") },
            confirmButton = { TextButton(onClick = { showLogoutDialog = false; onLogout() }) { Text("خروج", color = Color(0xFFA32D2D)) } },
            dismissButton = { TextButton({ showLogoutDialog = false }) { Text("إلغاء") } }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("الإعدادات") }, navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            // Profile card
            Card(colors = CardDefaults.cardColors(containerColor = BgCard), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(shape = CircleShape, color = Primary.copy(.15f), modifier = Modifier.size(60.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(uiState.userName.take(2), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(uiState.userName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(uiState.userPhone, fontSize = 13.sp, color = TextMuted)
                    Surface(shape = RoundedCornerShape(8.dp), color = Primary.copy(.1f), modifier = Modifier.padding(top = 6.dp)) {
                        Text(if (uiState.userRole == "merchant") "تاجر" else "عميل",
                            fontSize = 12.sp, color = Primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Settings items
            listOf(
                Triple(Icons.Default.Info, "إصدار التطبيق", "1.0.0"),
                Triple(Icons.Default.Phone, "رقم الهاتف", uiState.userPhone)
            ).forEach { (icon, title, value) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(icon, null, tint = TextMuted, modifier = Modifier.size(20.dp))
                        Text(title, fontSize = 14.sp)
                    }
                    Text(value, fontSize = 13.sp, color = TextMuted)
                }
                Divider(color = Color(0xFFF1EFE8))
            }

            Spacer(Modifier.weight(1f))
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA32D2D)),
                border = BorderStroke(1.dp, Color(0xFFA32D2D).copy(.4f)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("تسجيل الخروج", fontSize = 15.sp)
            }
        }
    }
}
