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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.app.model.AppUiState
import com.fintech.app.ui.theme.FinTechTheme
import com.fintech.app.ui.theme.Primary
import com.fintech.app.ui.theme.PrimaryDk
import com.fintech.app.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
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

@Preview(showBackground = true)
@Composable
fun TransferScreenPreview() {
    FinTechTheme {
        TransferScreen(
            uiState = AppUiState(balance = 75000.0),
            onTransfer = { _, _, _ -> },
            onBack = {},
            onClearError = {},
            onClearSuccess = {}
        )
    }
}
