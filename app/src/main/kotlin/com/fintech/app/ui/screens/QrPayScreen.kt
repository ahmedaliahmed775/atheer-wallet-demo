package com.fintech.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrPayScreen(
    uiState: AppUiState,
    onQrPay: (String, Double, String) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    var merchantPhone by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(uiState.lastQrPay) {
        if (uiState.lastQrPay != null) { merchantPhone = ""; amount = ""; note = "" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الدفع للتاجر") },
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
            // Info card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEDFE)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCodeScanner, null, tint = Merchant, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("الدفع عبر QR", fontWeight = FontWeight.SemiBold, color = Color(0xFF3C3489))
                        Text("أدخل رقم التاجر أو امسح الكود QR لإتمام الدفع", fontSize = 12.sp, color = Color(0xFF534AB7))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Balance
            Card(colors = CardDefaults.cardColors(containerColor = Primary), shape = RoundedCornerShape(14.dp)) {
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

            OutlinedTextField(
                value = merchantPhone,
                onValueChange = { merchantPhone = it; onClearError() },
                label = { Text("رقم هاتف التاجر") },
                leadingIcon = { Icon(Icons.Default.Store, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("ملاحظة (اختياري)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            uiState.error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCEBEB))) {
                    Text(err, color = Color(0xFFA32D2D), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            uiState.lastQrPay?.let { pay ->
                Spacer(Modifier.height(12.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5EE)), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = PrimaryDk)
                            Spacer(Modifier.width(8.dp))
                            Text("تم الدفع بنجاح", fontWeight = FontWeight.SemiBold, color = PrimaryDk)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("التاجر: ${pay.merchantName}", fontSize = 13.sp)
                        Text("المبلغ: %,.0f ﷼".format(pay.amount), fontSize = 13.sp)
                        Text("المرجع: ${pay.refId}", fontSize = 11.sp, color = TextMuted)
                        TextButton({ onClearSuccess() }) { Text("إغلاق") }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onQrPay(merchantPhone, amount.toDoubleOrNull() ?: 0.0, note.ifBlank { "" }) },
                enabled = !uiState.isLoading && merchantPhone.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Merchant),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else Text("ادفع الآن", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
\n@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
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
}\n