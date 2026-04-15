package com.fintech.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.*

private data class BillCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val providers: List<BillProvider>
)

private data class BillProvider(
    val id: String,
    val name: String
)

private val categories = listOf(
    BillCategory("TELECOM", "الاتصالات", Icons.Default.PhoneAndroid, Color(0xFF8B5CF6), listOf(
        BillProvider("yemen_mobile", "يمن موبايل"),
        BillProvider("you", "YOU"),
        BillProvider("sabafon", "سبأفون"),
        BillProvider("y_telecom", "واي"),
    )),
    BillCategory("INTERNET", "الإنترنت", Icons.Default.Wifi, Color(0xFF06B6D4), listOf(
        BillProvider("yemen_net", "يمن نت ADSL"),
        BillProvider("fiber", "الألياف الضوئية"),
    )),
    BillCategory("ELECTRICITY", "الكهرباء", Icons.Default.Bolt, Color(0xFFF59E0B), listOf(
        BillProvider("public_elec", "الكهرباء العامة"),
    )),
    BillCategory("WATER", "المياه", Icons.Default.WaterDrop, Color(0xFF3B82F6), listOf(
        BillProvider("public_water", "المياه والصرف"),
    )),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillPaymentScreen(
    uiState: AppUiState,
    onPayBill: (String, String, String, Double) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<BillCategory?>(null) }
    var selectedProvider by remember { mutableStateOf<BillProvider?>(null) }
    var accountNumber by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    LaunchedEffect(uiState.lastBillPayment) {
        if (uiState.lastBillPayment != null) {
            accountNumber = ""
            amount = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سداد الفواتير") },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Balance
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("رصيدك المتاح", color = Color.White.copy(.8f), fontSize = 13.sp)
                    Text("%,.0f ﷼".format(uiState.balance), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))

            if (selectedCategory == null) {
                // ─── Category Selection ────────────────────
                Text("اختر نوع الخدمة", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.height(12.dp))
                categories.forEach { cat ->
                    Card(
                        onClick = { selectedCategory = cat },
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(shape = CircleShape, color = cat.color.copy(.12f), modifier = Modifier.size(44.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(cat.icon, null, tint = cat.color, modifier = Modifier.size(22.dp))
                                }
                            }
                            Column(Modifier.weight(1f)) {
                                Text(cat.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text("${cat.providers.size} مزود خدمة", fontSize = 11.sp, color = TextMuted)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                        }
                    }
                }
            } else if (selectedProvider == null) {
                // ─── Provider Selection ────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton({ selectedCategory = null }) { Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(20.dp)) }
                    Text(selectedCategory!!.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text("اختر مقدم الخدمة", fontSize = 13.sp, color = TextMuted)
                Spacer(Modifier.height(8.dp))
                selectedCategory!!.providers.forEach { prov ->
                    Card(
                        onClick = { selectedProvider = prov },
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(shape = CircleShape, color = selectedCategory!!.color.copy(.12f), modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(selectedCategory!!.icon, null, tint = selectedCategory!!.color, modifier = Modifier.size(20.dp))
                                }
                            }
                            Text(prov.name, fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                        }
                    }
                }
            } else {
                // ─── Payment Form ──────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton({ selectedProvider = null }) { Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(20.dp)) }
                    Text("${selectedCategory!!.name} — ${selectedProvider!!.name}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Spacer(Modifier.height(16.dp))

                val accountLabel = when (selectedCategory!!.id) {
                    "TELECOM" -> "رقم الهاتف"
                    "INTERNET" -> "رقم الاشتراك"
                    else -> "رقم الحساب"
                }
                val keyboardType = if (selectedCategory!!.id == "TELECOM") KeyboardType.Phone else KeyboardType.Number

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it; onClearError() },
                    label = { Text(accountLabel) },
                    leadingIcon = { Icon(selectedCategory!!.icon, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it; onClearError() },
                    label = { Text("المبلغ (ريال يمني)") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                uiState.error?.let { err ->
                    Spacer(Modifier.height(8.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB))) {
                        Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                    }
                }

                uiState.lastBillPayment?.let { bill ->
                    Spacer(Modifier.height(12.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5EE)), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, null, tint = PrimaryDk)
                                Spacer(Modifier.width(8.dp))
                                Text("تم السداد بنجاح", fontWeight = FontWeight.SemiBold, color = PrimaryDk)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("الخدمة: ${bill.provider}", fontSize = 13.sp)
                            Text("الحساب: ${bill.accountNumber}", fontSize = 13.sp)
                            Text("المبلغ: %,.0f ﷼".format(bill.amount), fontSize = 13.sp)
                            Text("المرجع: ${bill.refId}", fontSize = 11.sp, color = TextMuted)
                            TextButton({
                                onClearSuccess()
                                selectedCategory = null
                                selectedProvider = null
                            }) { Text("عملية جديدة") }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        onPayBill(
                            selectedCategory!!.id,
                            selectedProvider!!.id,
                            accountNumber,
                            amount.toDoubleOrNull() ?: 0.0
                        )
                    },
                    enabled = !uiState.isLoading && accountNumber.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = selectedCategory!!.color),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    else Text("سداد الآن", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}


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
